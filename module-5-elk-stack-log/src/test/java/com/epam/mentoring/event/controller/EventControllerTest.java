package com.epam.mentoring.event.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.epam.mentoring.event.TestDataProvider;
import com.epam.mentoring.event.api.ElasticClient;
import com.epam.mentoring.event.dto.DeleteEventsResponseDto;
import com.epam.mentoring.event.model.Event;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "PT10S")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventControllerTest {

    private static final String URL_TEMPLATE = "/api/v1/events/";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ElasticClient elasticClient;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testSaveEvent_Ok() {
        final var upsertEventDto = TestDataProvider.getUpsertEventDtoStub();
        upsertEventDto.setScheduledTime(Instant.now().plus(1, ChronoUnit.DAYS));
        final var event = TestDataProvider.getEventStub();

        when(elasticClient.upsertEvent(event)).thenReturn(event.getId());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .build())
                .bodyValue(upsertEventDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testSaveEvent_BadRequest() {
        final var upsertEventDto = TestDataProvider.getUpsertEventDtoStub();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .build())
                .bodyValue(upsertEventDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testReplaceEvent_Ok() {
        final var upsertEventDto = TestDataProvider.getUpsertEventDtoStub();
        upsertEventDto.setScheduledTime(Instant.now().plus(1, ChronoUnit.DAYS));
        final var event = TestDataProvider.getEventStub();

        when(elasticClient.findEventById(event.getId())).thenReturn(event);
        when(elasticClient.upsertEvent(event)).thenReturn(event.getId());

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(event.getId())
                        .build())
                .bodyValue(upsertEventDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testReplaceEvent_BadRequest() {
        final var upsertEventDto = TestDataProvider.getUpsertEventDtoStub();
        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(UUID.randomUUID().toString())
                        .build())
                .bodyValue(upsertEventDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateEvent_Ok() {
        final var updateEventDto = TestDataProvider.getUpdateEventDto();
        final var event = TestDataProvider.getEventStub();

        when(elasticClient.findEventById(event.getId())).thenReturn(event);
        when(elasticClient.upsertEvent(event)).thenReturn(event.getId());

        webTestClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(event.getId())
                        .build())
                .bodyValue(updateEventDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testUpdateEvent_NotFound() {
        final var updateEventDto = TestDataProvider.getUpdateEventDto();
        final var event = TestDataProvider.getEventStub();

        when(elasticClient.findEventById(event.getId())).thenReturn(null);

        webTestClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(event.getId())
                        .build())
                .bodyValue(updateEventDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEvent_Ok() {
        final var id = UUID.randomUUID().toString();

        when(elasticClient.findEventById(id)).thenReturn(Event.builder().build());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(id)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteEvent_NotFound() {
        final var id = UUID.randomUUID().toString();

        when(elasticClient.findEventById(id)).thenReturn(null);

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(id)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEventsByTitle_Ok() throws JsonProcessingException {
        final var title = UUID.randomUUID().toString();

        when(elasticClient.deleteEventsByTitle(title)).thenReturn(2L);

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment("title")
                        .pathSegment(title)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(DeleteEventsResponseDto.builder().eventsDeleted(2L).build()));
    }

    @Test
    void testFindEventById_Ok() throws JsonProcessingException {
        final var event = TestDataProvider.getEventStub();

        when(elasticClient.findEventById(event.getId())).thenReturn(event);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(event.getId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(event));
    }

    @Test
    void testFindEventById_NotFound() throws JsonProcessingException {
        final var event = TestDataProvider.getEventStub();

        when(elasticClient.findEventById(event.getId())).thenReturn(null);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(event.getId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFindEventsByParameters() throws JsonProcessingException {
        final var findEventsDto = TestDataProvider.getFindEventsDtoStub();
        final var event = TestDataProvider.getEventStub();

        when(elasticClient.findEventsByParams(findEventsDto)).thenReturn(List.of(event));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .queryParam("title", findEventsDto.getTitle())
                        .queryParam("scheduledTimeFrom", findEventsDto.getScheduledTimeFrom())
                        .queryParam("type", findEventsDto.getType())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(List.of(event)));
    }

}
