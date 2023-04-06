package com.epam.mentoring.event.api;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.mentoring.event.TestDataProvider;
import com.epam.mentoring.event.exception.ElasticException;
import com.epam.mentoring.event.model.Event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class })
class ElasticApiClientTest {

    private ElasticApiClient elasticApiClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ElasticsearchClient elasticsearchClient;

    @BeforeEach
    void init() {
        final var indexMappings = Map.of("events", "\"title\": \"text\"");
        elasticApiClient = new ElasticApiClient(elasticsearchClient, indexMappings);
    }

    @Test
    void testCreateIndex_NotExists() throws IOException {
        final var createIndexResponse = TestDataProvider.getCreateIndexResponseStub(true);

        when(elasticsearchClient.indices().exists(any(Function.class))).thenReturn(new BooleanResponse(false));
        when(elasticsearchClient.indices().create(any(Function.class))).thenReturn(createIndexResponse);

        assertDoesNotThrow(() -> elasticApiClient.createIndex());
    }

    @Test
    void testCreateIndex_AlreadyExists() throws IOException {
        when(elasticsearchClient.indices().exists(any(Function.class))).thenReturn(new BooleanResponse(true));

        assertDoesNotThrow(() -> elasticApiClient.createIndex());
        verify(elasticsearchClient, never()).create(any(Function.class));
    }

    @Test
    void testCreateIndex_ElasticThrowsException() throws IOException {
        when(elasticsearchClient.indices().create(any(Function.class))).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticApiClient.createIndex()).isInstanceOf(ElasticException.class);
    }

    @Test
    void testCreateIndex_AcknowledgeFalse() throws IOException {
        final var createIndexResponse = TestDataProvider.getCreateIndexResponseStub(false);

        when(elasticsearchClient.indices().create(any(Function.class))).thenReturn(createIndexResponse);

        assertThatThrownBy(() -> elasticApiClient.createIndex()).isInstanceOf(ElasticException.class);
    }

    @Test
    void testUpdateIndexMappings() throws IOException {
        final var putMappingResponse = TestDataProvider.getPutMappingResponseStub(true);

        when(elasticsearchClient.indices().putMapping(any(Function.class))).thenReturn(putMappingResponse);

        assertDoesNotThrow(() -> elasticApiClient.updateIndexMappings());
    }

    @Test
    void testUpdateIndexMappings_ElasticThrowsException() throws IOException {
        when(elasticsearchClient.indices().putMapping(any(Function.class))).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticApiClient.updateIndexMappings()).isInstanceOf(ElasticException.class);
    }

    @Test
    void testUpdateIndexMappings_AcknowledgeFalse() throws IOException {
        final var putMappingResponse = TestDataProvider.getPutMappingResponseStub(false);

        when(elasticsearchClient.indices().putMapping(any(Function.class))).thenReturn(putMappingResponse);

        assertThatThrownBy(() -> elasticApiClient.updateIndexMappings()).isInstanceOf(ElasticException.class);
    }

    @Test
    void testUpsertEvent() throws IOException {
        final var indexResponse = TestDataProvider.getIndexResponseStub();
        final var event = TestDataProvider.getEventStub();

        when(elasticsearchClient.index(any(Function.class))).thenReturn(indexResponse);

        final var actual = elasticApiClient.upsertEvent(event);

        assertThat(actual).isEqualTo(event.getId());
    }

    @Test
    void testUpsertEvent_ElasticThrowsException() throws IOException {
        final var event = TestDataProvider.getEventStub();

        when(elasticsearchClient.index(any(Function.class))).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticApiClient.upsertEvent(event)).isInstanceOf(ElasticException.class);
    }

    @Test
    void testFindEventById() throws IOException {
        final var event = TestDataProvider.getEventStub();
        final var getResponse = TestDataProvider.getGetResponseStub(event);

        when(elasticsearchClient.get(any(Function.class), eq(Event.class))).thenReturn(getResponse);

        final var actual = elasticApiClient.findEventById(event.getId());

        assertThat(actual).isEqualTo(event);
    }

    @Test
    void testFindEventById_ElasticThrowsException() throws IOException {
        when(elasticsearchClient.get(any(Function.class), eq(Event.class))).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticApiClient.findEventById(UUID.randomUUID().toString())).isInstanceOf(
                ElasticException.class);
    }

    @Test
    void testFindEventsByParams() throws IOException {
        final var findEventsDto = TestDataProvider.getFindEventsDtoStub();
        final var event = TestDataProvider.getEventStub();
        final var searchResponse = TestDataProvider.getSearchResponseStub(event);

        when(elasticsearchClient.search(any(Function.class), eq(Event.class))).thenReturn(searchResponse);

        final var actual = elasticApiClient.findEventsByParams(findEventsDto);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(event);
    }

    @Test
    void testFindEventsByParams_ElasticThrowsException() throws IOException {
        final var findEventsDto = TestDataProvider.getFindEventsDtoStub();

        when(elasticsearchClient.search(any(Function.class), eq(Event.class))).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticApiClient.findEventsByParams(findEventsDto)).isInstanceOf(
                ElasticException.class);
    }

    @Test
    void testDeleteEventById() throws IOException {
        final var deleteResponse = TestDataProvider.getDeleteResponseStub();

        when(elasticsearchClient.delete(any(Function.class))).thenReturn(deleteResponse);

        assertDoesNotThrow(() -> elasticApiClient.deleteEventById(UUID.randomUUID().toString()));
    }

    @Test
    void testDeleteEventById_ElasticThrowsException() throws IOException {
        when(elasticsearchClient.delete(any(Function.class))).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticApiClient.deleteEventById(UUID.randomUUID().toString())).isInstanceOf(
                ElasticException.class);
    }

    @Test
    void testDeleteEventsByTitle() throws IOException {
        final var deleteByQueryResponse = TestDataProvider.getDeleteByQueryResponseStub();

        when(elasticsearchClient.deleteByQuery(any(Function.class))).thenReturn(deleteByQueryResponse);

        final var actual = elasticApiClient.deleteEventsByTitle("Event Title");

        assertThat(actual).isEqualTo(2L);
    }

    @Test
    void testDeleteEventsByTitle_ElasticThrowsException() throws IOException {
        when(elasticsearchClient.deleteByQuery(any(Function.class))).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticApiClient.deleteEventsByTitle("Event Title")).isInstanceOf(
                ElasticException.class);
    }

}
