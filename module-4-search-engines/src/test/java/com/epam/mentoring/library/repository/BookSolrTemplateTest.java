package com.epam.mentoring.library.repository;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.schema.SchemaOperations;

import com.epam.mentoring.library.TestDataProvider;
import com.epam.mentoring.library.exception.SolrException;
import com.epam.mentoring.library.model.Book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class })
class BookSolrTemplateTest {

    private static final String COLLECTION_NAME = "books";

    private BookSolrTemplate bookSolrTemplate;

    @Mock
    private SolrTemplate solrTemplate;

    @Mock
    private SolrClient solrClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SchemaOperations schemaOperations;

    @BeforeEach
    void init() {
        bookSolrTemplate = new BookSolrTemplate(solrTemplate);
    }

    @Test
    void testUpdateSchemaWithCopyFields() {
        when(solrTemplate.getSchemaOperations(COLLECTION_NAME)).thenReturn(schemaOperations);
        when(schemaOperations.readSchema().containsField("suggestion")).thenReturn(false);

        bookSolrTemplate.updateSchemaWithCopyFields();

        verify(schemaOperations, times(3)).addField(any());
    }

    @Test
    void testFindBooksByParameters() {
        final var findBooksRequestDto = TestDataProvider.getFindBooksRequestDtoStub();
        final var expected = TestDataProvider.getFacetPageBookStub();

        when(solrTemplate.queryForFacetPage(eq(COLLECTION_NAME), any(), eq(Book.class))).thenReturn(expected);

        final var actual = bookSolrTemplate.findBooksByParameters(findBooksRequestDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetSuggestions() throws SolrServerException, IOException {
        final var query = "auth";
        final var suggesterResponse = new SimpleOrderedMap<>();
        suggesterResponse.add("suggest", new SimpleOrderedMap<>());
        final var queryResponse = new QueryResponse();
        queryResponse.setResponse(suggesterResponse);

        when(solrTemplate.getSolrClient()).thenReturn(solrClient);
        when(solrClient.query(eq(COLLECTION_NAME), any())).thenReturn(queryResponse);

        assertThat(bookSolrTemplate.getSuggestions(query)).isNotNull();
    }

    @Test
    void testGetSuggestions_SolrIO_Exception() throws SolrServerException, IOException {
        final var query = "auth";

        when(solrTemplate.getSolrClient()).thenReturn(solrClient);
        when(solrClient.query(eq(COLLECTION_NAME), any())).thenThrow(new IOException());

        assertThatThrownBy(() -> bookSolrTemplate.getSuggestions(query)).isInstanceOf(SolrException.class);
    }

}
