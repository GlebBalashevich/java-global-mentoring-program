package com.epam.mentoring.event.api;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.epam.mentoring.event.dto.FindEventsDto;
import com.epam.mentoring.event.dto.elastic.BoolQueryDto;
import com.epam.mentoring.event.dto.elastic.CreateIndexResponseDto;
import com.epam.mentoring.event.dto.elastic.DeleteEventByIdResponseDto;
import com.epam.mentoring.event.dto.elastic.DeleteEventsByTitleResponseDto;
import com.epam.mentoring.event.dto.elastic.FilterDto;
import com.epam.mentoring.event.dto.elastic.FindEventByIdResponseDto;
import com.epam.mentoring.event.dto.elastic.HitResponseDto;
import com.epam.mentoring.event.dto.elastic.MustDto;
import com.epam.mentoring.event.dto.elastic.QueryDto;
import com.epam.mentoring.event.dto.elastic.QueryRequestBodyDto;
import com.epam.mentoring.event.dto.elastic.RangeDto;
import com.epam.mentoring.event.dto.elastic.SearchEventsResponseDto;
import com.epam.mentoring.event.dto.elastic.UpdateMappingsResponseDto;
import com.epam.mentoring.event.dto.elastic.UpsertEventResponseDto;
import com.epam.mentoring.event.exception.ElasticException;
import com.epam.mentoring.event.model.Event;
import com.epam.mentoring.event.util.ElasticUtil;
import com.epam.mentoring.event.util.ErrorCode;

@Slf4j
@Profile("rest-client")
@Component
@RequiredArgsConstructor
public class ElasticRestClient implements ElasticClient {

    private static final String INDEX_NAME = "events";

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    private final Map<String, String> indexMappings;

    @Override
    public void createIndex() {
        try {
            final var existsRequest = buildRequest(HttpMethod.HEAD);
            if (restClient.performRequest(existsRequest).getStatusLine()
                    .getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                final var createIndexRequest = buildRequest(HttpMethod.PUT);
                final var httpResponse = restClient.performRequest(createIndexRequest);
                final var response = mapToObject(httpResponse.getEntity().getContent(), CreateIndexResponseDto.class);
                if (!response.isAcknowledged()) {
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
            final var request = buildRequestWithBody(HttpMethod.PUT, indexMappingJson, ElasticUtil.Resource.MAPPING);
            final var httpResponse = restClient.performRequest(request);
            final var response = mapToObject(httpResponse.getEntity().getContent(), UpdateMappingsResponseDto.class);
            if (!response.isAcknowledged()) {
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
            final var eventJson = objectMapper.writeValueAsString(event);
            final var request = buildRequestWithBody(HttpMethod.POST, eventJson, ElasticUtil.Resource.DOC,
                    event.getId());
            final var httpResponse = restClient.performRequest(request);
            final var response = mapToObject(httpResponse.getEntity().getContent(), UpsertEventResponseDto.class);
            log.debug("Result of upserting document with id: {} - {}", event.getId(), response.getResult());
            return response.getId();
        } catch (IOException e) {
            throw error(String.format("Error occurred during indexing document %s", event), e);
        }
    }

    @Override
    public Event findEventById(String id) {
        try {
            final var request = buildRequest(HttpMethod.GET, ElasticUtil.Resource.DOC, id);
            final var httpResponse = restClient.performRequest(request);
            final var response = mapToObject(httpResponse.getEntity().getContent(), FindEventByIdResponseDto.class);
            log.debug("Found status for id: {} - {}", id, response.isFound());
            return response.getEvent();
        } catch (IOException e) {
            throw error(String.format("Error occurred during searching document by id %s", id), e);
        }
    }

    @Override
    public List<Event> findEventsByParams(FindEventsDto findEvents) {
        try {
            final var searchQuery = buildSearchQuery(findEvents);
            final var request = buildRequestWithBody(HttpMethod.GET, searchQuery, ElasticUtil.Resource.SEARCH);
            final var httpResponse = restClient.performRequest(request);
            final var response = mapToObject(httpResponse.getEntity().getContent(), SearchEventsResponseDto.class);
            return response.getTotalHit().getHits().stream().map(HitResponseDto::getEvent).toList();
        } catch (IOException e) {
            throw error(String.format("Error occurred during searching document by params: %s", findEvents), e);
        }
    }

    @Override
    public void deleteEventById(String id) {
        try {
            final var request = buildRequest(HttpMethod.DELETE, ElasticUtil.Resource.DOC, id);
            final var httpResponse = restClient.performRequest(request);
            final var response = mapToObject(httpResponse.getEntity().getContent(), DeleteEventByIdResponseDto.class);
            log.debug("Result of deleting document by id: {} - {}", id, response.getResult());
        } catch (IOException e) {
            throw error(String.format("Error occurred during deleting document by id %s", id), e);
        }
    }

    @Override
    public Long deleteEventsByTitle(String title) {
        try {
            final var deleteQuery = buildDeleteQuery(title);
            final var request = buildRequestWithBody(HttpMethod.POST, deleteQuery,
                    ElasticUtil.Resource.DELETE_BY_QUERY);
            final var httpResponse = restClient.performRequest(request);
            final var response = mapToObject(httpResponse.getEntity().getContent(),
                    DeleteEventsByTitleResponseDto.class);
            log.debug("{} - documents were deleted", response.getDeleted());
            return response.getDeleted();
        } catch (IOException e) {
            throw error(String.format("Error occurred during deleting document by title %s", title), e);
        }
    }

    private Request buildRequest(HttpMethod method, String... paths) {
        final var path = INDEX_NAME.concat("/").concat(String.join("/", paths));
        final var request = new Request(method.name(), path);
        request.addParameter("ignore", "404");
        return request;
    }

    private Request buildRequestWithBody(HttpMethod method, String body, String... paths) {
        final var request = buildRequest(method, paths);
        request.setJsonEntity(body);
        return request;
    }

    private String buildSearchQuery(FindEventsDto findEvents) throws JsonProcessingException {
        final var boolQuery = BoolQueryDto.builder();
        final var mustList = new ArrayList<MustDto>();
        if (StringUtils.hasText(findEvents.getTitle())) {
            log.debug("Title - {} was added to search query", findEvents.getTitle());
            mustList.add(MustDto.builder().match(Map.of(ElasticUtil.Field.TITLE, findEvents.getTitle())).build());
        }
        if (findEvents.getType() != null) {
            log.debug("Type - {} was added to search query", findEvents.getType());
            mustList.add(MustDto.builder().match(Map.of(ElasticUtil.Field.TYPE, findEvents.getType().name())).build());
        }
        if (findEvents.getScheduledTimeFrom() != null) {
            log.debug("Time GT filter with value - {} was added to search query", findEvents.getScheduledTimeFrom());
            final var range = RangeDto.builder().gt(findEvents.getScheduledTimeFrom().toString())
                    .lt(Instant.now().toString()).build();
            boolQuery.filter(FilterDto.builder().range(Map.of(ElasticUtil.Field.SCHEDULED_TIME, range)).build());
        }
        boolQuery.must(mustList);
        final var searchQuery = QueryRequestBodyDto.builder()
                .query(QueryDto.builder().booleanQuery(boolQuery.build()).build()).build();
        return objectMapper.writeValueAsString(searchQuery);
    }

    private String buildDeleteQuery(String title) throws JsonProcessingException {
        final var must = MustDto.builder().match(Map.of(ElasticUtil.Field.TITLE, title)).build();
        final var deleteQuery = QueryRequestBodyDto.builder()
                .query(QueryDto.builder().booleanQuery(BoolQueryDto.builder().must(List.of(must)).build()).build())
                .build();
        return objectMapper.writeValueAsString(deleteQuery);
    }

    private ElasticException error(String message, HttpStatus httpStatus, String errorCode) {
        log.error(message);
        return new ElasticException(message, httpStatus, errorCode);
    }

    private ElasticException error(String message, Throwable e) {
        log.error(message, e);
        return new ElasticException(message, e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ELASTIC_IO_ERROR);
    }

    private <T> T mapToObject(InputStream stream, Class<T> clazz) throws IOException {
        return objectMapper.readValue(stream, clazz);
    }

}
