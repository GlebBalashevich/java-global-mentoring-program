package com.epam.mentoring.event.api;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.epam.mentoring.event.dto.FindEventsDto;
import com.epam.mentoring.event.exception.ElasticException;
import com.epam.mentoring.event.model.Event;
import com.epam.mentoring.event.util.ElasticUtil;
import com.epam.mentoring.event.util.ErrorCode;

@Slf4j
@Profile("!rest-client")
@Component
@RequiredArgsConstructor
public class ElasticApiClient implements ElasticClient {

    private static final String INDEX_NAME = "events";

    private final ElasticsearchClient elasticsearchClient;

    private final Map<String, String> indexMappings;

    @Override
    public void createIndex() {
        try {
            if (!isIndexExists()) {
                final var response = elasticsearchClient.indices().create(index -> index.index(INDEX_NAME));
                if (!response.acknowledged()) {
                    throw error(String.format("It is unable to create an index with the name: %s", INDEX_NAME),
                            HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ELASTIC_SERVER_ERROR);
                }
            }
        } catch (IOException e) {
            throw error(String.format("Error occurred during the index %s creation", INDEX_NAME), e);
        }
    }

    @Override
    public void updateIndexMappings() {
        try {
            final var indexMappingJson = Optional.of(indexMappings.get(INDEX_NAME)).orElseThrow(
                    () -> error(String.format("Mapping for index name %s not found", INDEX_NAME), HttpStatus.NOT_FOUND,
                            ErrorCode.ELASTIC_NOT_FOUND));
            final var response = elasticsearchClient.indices()
                    .putMapping(mapping -> mapping.index(INDEX_NAME).withJson(new StringReader(indexMappingJson)));
            if (!response.acknowledged()) {
                throw error(String.format("It is unable to put mappings for index: %s", INDEX_NAME),
                        HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ELASTIC_SERVER_ERROR);
            }
        } catch (IOException e) {
            throw error(String.format("Error occurred during mapping update for index %s", INDEX_NAME), e);
        }
    }

    @Override
    public String upsertEvent(Event event) {
        try {
            final var response = elasticsearchClient.index(
                    index -> index.index(INDEX_NAME).id(event.getId()).document(event));
            log.debug("Result of upserting document with id: {} - {}", event.getId(), response.result().jsonValue());
            return response.id();
        } catch (IOException e) {
            throw error(String.format("Error occurred during indexing document %s", event), e);
        }
    }

    @Override
    public Event findEventById(String id) {
        try {
            final var response = elasticsearchClient.get(request -> request.index(INDEX_NAME).id(id), Event.class);
            log.debug("Found status for id: {} - {}", id, response.found());
            return response.source();
        } catch (IOException e) {
            throw error(String.format("Error occurred during searching document by id %s", id), e);
        }
    }

    @Override
    public List<Event> findEventsByParams(FindEventsDto findEvents) {
        try {
            final var response = elasticsearchClient.search(
                    request -> request.index(INDEX_NAME).query(buildSearchQuery(findEvents)), Event.class);
            return response.hits().hits().stream().map(Hit::source).toList();
        } catch (IOException e) {
            throw error(String.format("Error occurred during searching document by params: %s", findEvents), e);
        }
    }

    @Override
    public void deleteEventById(String id) {
        try {
            final var result = elasticsearchClient.delete(request -> request.index(INDEX_NAME).id(id)).result();
            log.debug("Result of deleting document by id: {} - {}", id, result.jsonValue());
        } catch (IOException e) {
            throw error(String.format("Error occurred during deleting document by id %s", id), e);
        }
    }

    @Override
    public Long deleteEventsByTitle(String title) {
        try {
            final var deleted = elasticsearchClient.deleteByQuery(
                    request -> request.index(INDEX_NAME).query(buildDeleteQuery(title))).deleted();
            log.debug("{} - documents were deleted", deleted);
            return deleted;
        } catch (IOException e) {
            throw error(String.format("Error occurred during deleting document by title %s", title), e);
        }
    }

    private boolean isIndexExists() throws IOException {
        final var response = elasticsearchClient.indices().exists(request -> request.index(INDEX_NAME));
        log.debug("Index: {} exist status - {}", INDEX_NAME, response.value());
        return response.value();
    }

    private Query buildSearchQuery(FindEventsDto findEvents) {
        final var boolQuery = new BoolQuery.Builder();
        if (StringUtils.hasText(findEvents.getTitle())) {
            log.debug("Title - {} was added to search query", findEvents.getTitle());
            boolQuery.must(
                    must -> must.match(match -> match.field(ElasticUtil.Field.TITLE).query(findEvents.getTitle())));
        }
        if (findEvents.getType() != null) {
            log.debug("Type - {} was added to search query", findEvents.getType());
            boolQuery.must(must -> must.match(
                    match -> match.field(ElasticUtil.Field.TYPE).query(findEvents.getType().name())));
        }
        if (findEvents.getScheduledTimeFrom() != null) {
            log.debug("Time GT filter with value - {} was added to search query", findEvents.getScheduledTimeFrom());
            boolQuery.filter(filter -> filter.range(
                    range -> range.field(ElasticUtil.Field.SCHEDULED_TIME)
                            .gt(JsonData.of(findEvents.getScheduledTimeFrom()))
                            .lt(JsonData.of(Instant.now()))));
        }
        return new Query.Builder().bool(boolQuery.build()).build();
    }

    private Query buildDeleteQuery(String title) {
        final var boolQuery = new BoolQuery.Builder().must(
                must -> must.match(match -> match.field(ElasticUtil.Field.TITLE).query(title))).build();
        return new Query.Builder().bool(boolQuery).build();
    }

    private ElasticException error(String message, HttpStatus httpStatus, String errorCode) {
        log.error(message);
        return new ElasticException(message, httpStatus, errorCode);
    }

    private ElasticException error(String message, Throwable e) {
        log.error(message, e);
        return new ElasticException(message, e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ELASTIC_IO_ERROR);
    }

}
