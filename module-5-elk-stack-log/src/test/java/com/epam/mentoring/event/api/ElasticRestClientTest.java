package com.epam.mentoring.event.api;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.mentoring.event.TestDataProvider;
import com.epam.mentoring.event.exception.ElasticException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class })
class ElasticRestClientTest {

    private ElasticRestClient elasticRestClient;

    @Mock
    private RestClient restClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Response response;

    @BeforeEach
    void init() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(InstantSerializer.INSTANCE);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module())
                .registerModule(javaTimeModule).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        final var indexMappings = Map.of("events", "\"title\": \"text\"");
        elasticRestClient = new ElasticRestClient(restClient, objectMapper, indexMappings);
    }

    @Test
    void testCreateIndex_NotExists() throws IOException {
        final var createIndexContentStream = TestDataProvider.getCreateIndexContentStreamStub(true);

        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getStatusLine().getStatusCode()).thenReturn(404);
        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getEntity().getContent()).thenReturn(createIndexContentStream);

        assertDoesNotThrow(() -> elasticRestClient.createIndex());
    }

    @Test
    void testCreateIndex_AcknowledgeFalse() throws IOException {
        final var createIndexContentStream = TestDataProvider.getCreateIndexContentStreamStub(false);

        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getStatusLine().getStatusCode()).thenReturn(404);
        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getEntity().getContent()).thenReturn(createIndexContentStream);

        assertThatThrownBy(() -> elasticRestClient.createIndex()).isInstanceOf(ElasticException.class);
    }

    @Test
    void testCreateIndex_AlreadyExists() throws IOException {
        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getStatusLine().getStatusCode()).thenReturn(200);

        assertDoesNotThrow(() -> elasticRestClient.createIndex());
        verify(restClient).performRequest(any());
    }

    @Test
    void testCreateIndex_ElasticThrowsException() throws IOException {
        when(restClient.performRequest(any())).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticRestClient.createIndex()).isInstanceOf(ElasticException.class);
    }

    @Test
    void testUpdateIndexMappings() throws IOException {
        final var putMappingResponse = TestDataProvider.getUpdateMappingsContentStreamStub(true);

        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getEntity().getContent()).thenReturn(putMappingResponse);

        assertDoesNotThrow(() -> elasticRestClient.updateIndexMappings());
    }

    @Test
    void testUpdateIndexMappings_AcknowledgeFalse() throws IOException {
        final var putMappingResponse = TestDataProvider.getUpdateMappingsContentStreamStub(false);

        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getEntity().getContent()).thenReturn(putMappingResponse);

        assertThatThrownBy(() -> elasticRestClient.updateIndexMappings()).isInstanceOf(ElasticException.class);
    }

    @Test
    void testUpdateIndexMappings_ElasticThrowsException() throws IOException {
        when(restClient.performRequest(any())).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticRestClient.updateIndexMappings()).isInstanceOf(ElasticException.class);
    }

    @Test
    void testUpsertEvent() throws IOException {
        final var event = TestDataProvider.getEventStub();
        final var indexEventResponse = TestDataProvider.getIndexEventContentStreamStub(event.getId());

        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getEntity().getContent()).thenReturn(indexEventResponse);

        final var actual = elasticRestClient.upsertEvent(event);

        assertThat(actual).isEqualTo(event.getId());
    }

    @Test
    void testUpsertEvent_ElasticThrowsException() throws IOException {
        final var event = TestDataProvider.getEventStub();

        when(restClient.performRequest(any())).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticRestClient.upsertEvent(event)).isInstanceOf(ElasticException.class);
    }

    @Test
    void testFindEventById() throws IOException {
        final var event = TestDataProvider.getEventStub();
        final var findEventContent = TestDataProvider.getFindEventByIdResponseContentStub(event);

        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getEntity().getContent()).thenReturn(findEventContent);

        final var actual = elasticRestClient.findEventById(event.getId());

        assertThat(actual).isEqualTo(event);
    }

    @Test
    void testFindEventById_ElasticThrowsException() throws IOException {
        when(restClient.performRequest(any())).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticRestClient.findEventById(UUID.randomUUID().toString())).isInstanceOf(
                ElasticException.class);
    }

    @Test
    void testFindEventsByParams() throws IOException {
        final var findEventsDto = TestDataProvider.getFindEventsDtoStub();
        final var event = TestDataProvider.getEventStub();
        final var findEventsResponse = TestDataProvider.getFindEventsByParametersResponseContentStub(event);

        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getEntity().getContent()).thenReturn(findEventsResponse);

        final var actual = elasticRestClient.findEventsByParams(findEventsDto);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(event);
    }

    @Test
    void testFindEventsByParams_ElasticThrowsException() throws IOException {
        final var findEventsDto = TestDataProvider.getFindEventsDtoStub();

        when(restClient.performRequest(any())).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticRestClient.findEventsByParams(findEventsDto)).isInstanceOf(
                ElasticException.class);
    }

    @Test
    void testDeleteEventById() throws IOException {
        final var id = "008e6bbc-7ad3-4a8c-a3c0-658aad072c67";
        final var deleteEventResponse = TestDataProvider.getDeleteByIdResponseContentStreamStub(id);

        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getEntity().getContent()).thenReturn(deleteEventResponse);

        assertDoesNotThrow(() -> elasticRestClient.deleteEventById(id));
    }

    @Test
    void testDeleteEventById_ElasticThrowsException() throws IOException {
        when(restClient.performRequest(any())).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticRestClient.deleteEventById(UUID.randomUUID().toString())).isInstanceOf(
                ElasticException.class);
    }

    @Test
    void testDeleteEventsByTitle() throws IOException {
        final var deleteByQueryResponse = TestDataProvider.getDeleteByQueryResponseContentStreamStub();

        when(restClient.performRequest(any())).thenReturn(response);
        when(response.getEntity().getContent()).thenReturn(deleteByQueryResponse);

        final var actual = elasticRestClient.deleteEventsByTitle("Event Title");

        assertThat(actual).isEqualTo(2L);
    }

    @Test
    void testDeleteEventsByTitle_ElasticThrowsException() throws IOException {
        when(restClient.performRequest(any())).thenThrow(IOException.class);

        assertThatThrownBy(() -> elasticRestClient.deleteEventsByTitle("Event Title")).isInstanceOf(
                ElasticException.class);
    }

}
