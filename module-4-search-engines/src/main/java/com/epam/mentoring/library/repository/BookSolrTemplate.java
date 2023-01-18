package com.epam.mentoring.library.repository;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.schema.SchemaDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.epam.mentoring.library.dto.FindBooksRequestDto;
import com.epam.mentoring.library.exception.SolrException;
import com.epam.mentoring.library.model.Book;
import com.epam.mentoring.library.util.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookSolrTemplate {

    private static final String COLLECTION_NAME = "books";

    private static final String SUGGESTION_FIELD = "suggestion";

    private static final String TITLE_FIELD = "title";

    private static final String AUTHORS_FIELD = "authors";

    private static final String SUGGESTION_HANDLER = "/suggest";

    private static final String SUGGESTION_BUILD_PARAM = "suggest.build";

    private final SolrTemplate solrTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void updateSchemaWithCopyFields() {
        final var schemaOperations = solrTemplate.getSchemaOperations(COLLECTION_NAME);
        if (!schemaOperations.readSchema().containsField(SUGGESTION_FIELD)) {
            log.debug("Schema doesn't contain field suggestion, updating schema...");
            final var suggestionField = SchemaDefinition.FieldDefinition.newFieldDefinition().named(SUGGESTION_FIELD)
                    .typedAs("string").indexed().stored().muliValued().create();
            final var copyTitleField = SchemaDefinition.CopyFieldDefinition.newCopyFieldDefinition()
                    .copyFrom(TITLE_FIELD).to(SUGGESTION_FIELD).create();
            final var copyAuthorsField = SchemaDefinition.CopyFieldDefinition.newCopyFieldDefinition()
                    .copyFrom(AUTHORS_FIELD).to(SUGGESTION_FIELD).create();
            schemaOperations.addField(suggestionField);
            schemaOperations.addField(copyTitleField);
            schemaOperations.addField(copyAuthorsField);
        }
    }

    public FacetPage<Book> findBooksByParameters(FindBooksRequestDto findBooksRequestDto) {
        final var query = buildFindBooksQuery(findBooksRequestDto);
        return solrTemplate.queryForFacetPage(COLLECTION_NAME, query, Book.class);
    }

    public SuggesterResponse getSuggestions(String query) {
        final var solrClient = solrTemplate.getSolrClient();
        final var solrQuery = buildSuggestionQuery(query);
        QueryResponse queryResponse;
        try {
            queryResponse = solrClient.query(COLLECTION_NAME, solrQuery);
        } catch (SolrServerException | IOException e) {
            log.error("Error occurs while processing suggest request for query: {}", query, e);
            throw new SolrException("Unable to process suggestion request for query:" + query, e,
                    HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.SOLR_SUGGESTION_ERROR);
        }
        return queryResponse.getSuggesterResponse();
    }

    private FacetQuery buildFindBooksQuery(FindBooksRequestDto findBooksRequestDto) {
        Criteria criteria;
        if (Boolean.TRUE.equals(findBooksRequestDto.getFulltext())) {
            criteria = new Criteria().expression(findBooksRequestDto.getQuery());
        } else if (StringUtils.hasText(findBooksRequestDto.getField())) {
            criteria = Criteria.where(findBooksRequestDto.getField()).is(findBooksRequestDto.getValue());
        } else {
            criteria = Criteria.where(Criteria.WILDCARD).is(Criteria.WILDCARD);
        }
        final var query = new SimpleFacetQuery(criteria);
        if (StringUtils.hasText(findBooksRequestDto.getFacetField())) {
            query.setFacetOptions(new FacetOptions(findBooksRequestDto.getFacetField()));
        }
        return query;
    }

    private SolrQuery buildSuggestionQuery(String query) {
        final var solrQuery = new SolrQuery();
        solrQuery.setRequestHandler(SUGGESTION_HANDLER);
        solrQuery.setParam(SUGGESTION_BUILD_PARAM, Boolean.TRUE);
        solrQuery.setQuery(query);
        return solrQuery;
    }

}
