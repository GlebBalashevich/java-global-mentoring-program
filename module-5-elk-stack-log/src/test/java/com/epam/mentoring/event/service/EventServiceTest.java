package com.epam.mentoring.event.service;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.mentoring.event.TestDataProvider;
import com.epam.mentoring.event.api.ElasticClient;
import com.epam.mentoring.event.exception.EventException;
import com.epam.mentoring.event.mapper.EventMapper;
import com.epam.mentoring.event.model.EventType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class })
class EventServiceTest {

    private EventService eventService;

    @Mock
    private ElasticClient elasticClient;

    @Mock
    private EventMapper eventMapper;

    @BeforeEach
    void init() {
        eventService = new EventService(elasticClient, eventMapper);
    }

    @Test
    void testInitializeIndex() {
        eventService.initializeIndex();

        verify(elasticClient).createIndex();
        verify(elasticClient).updateIndexMappings();
    }

    @Test
    void testSaveEvent() {
        final var upsertEventDto = TestDataProvider.getUpsertEventDtoStub();
        final var event = TestDataProvider.getEventStub();
        final var expected = TestDataProvider.getEventDtoStub();

        when(eventMapper.toEvent(any(), eq(upsertEventDto))).thenReturn(event);
        when(elasticClient.upsertEvent(event)).thenReturn(event.getId());
        when(eventMapper.toEventDto(event)).thenReturn(expected);

        final var actual = eventService.saveEvent(upsertEventDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testReplaceEvent() {
        final var eventId = "008e6bbc-7ad3-4a8c-a3c0-658aad072c67";
        final var upsertEventDto = TestDataProvider.getUpsertEventDtoStub();
        final var event = TestDataProvider.getEventStub();
        final var expected = TestDataProvider.getEventDtoStub();

        when(elasticClient.findEventById(eventId)).thenReturn(event);
        when(eventMapper.toEvent(eventId, upsertEventDto)).thenReturn(event);
        when(elasticClient.upsertEvent(event)).thenReturn(eventId);
        when(eventMapper.toEventDto(event)).thenReturn(expected);

        final var actual = eventService.replaceEvent(eventId, upsertEventDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testReplaceEvent_EventNotFound() {
        final var eventId = UUID.randomUUID().toString();
        final var upsertEventDto = TestDataProvider.getUpsertEventDtoStub();

        when(elasticClient.findEventById(eventId)).thenReturn(null);

        assertThatThrownBy(() -> eventService.replaceEvent(eventId, upsertEventDto)).isInstanceOf(EventException.class);
    }

    @Test
    void testUpdateEvent() {
        final var eventId = "008e6bbc-7ad3-4a8c-a3c0-658aad072c67";
        final var updateEventDto = TestDataProvider.getUpdateEventDto();
        final var existingEvent = TestDataProvider.getEventStub();
        final var updatingEvent = TestDataProvider.getEventStub();
        updatingEvent.setTitle("New Event");
        updatingEvent.setType(EventType.TECH_TALK);
        final var expected = TestDataProvider.getEventDtoStub();

        when(elasticClient.findEventById(eventId)).thenReturn(existingEvent);
        when(elasticClient.upsertEvent(updatingEvent)).thenReturn(eventId);
        when(eventMapper.toEventDto(updatingEvent)).thenReturn(expected);

        final var actual = eventService.updateEvent(eventId, updateEventDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testUpdateEvent_EventNotFound() {
        final var eventId = UUID.randomUUID().toString();
        final var updateEventDto = TestDataProvider.getUpdateEventDto();

        when(elasticClient.findEventById(eventId)).thenReturn(null);

        assertThatThrownBy(() -> eventService.updateEvent(eventId, updateEventDto)).isInstanceOf(EventException.class);
    }

    @Test
    void testDeleteEventById() {
        final var eventId = "008e6bbc-7ad3-4a8c-a3c0-658aad072c67";
        final var event = TestDataProvider.getEventStub();

        when(elasticClient.findEventById(eventId)).thenReturn(event);

        eventService.deleteEventById(eventId);

        verify(elasticClient).deleteEventById(eventId);
    }

    @Test
    void testDeleteEventById_EventNotFound() {
        final var eventId = UUID.randomUUID().toString();

        when(elasticClient.findEventById(eventId)).thenReturn(null);

        assertThatThrownBy(() -> eventService.deleteEventById(eventId)).isInstanceOf(EventException.class);
    }

    @Test
    void testDeleteEventsByTitle() {
        final var eventsTitle = "Event Title";
        when(elasticClient.deleteEventsByTitle(eventsTitle)).thenReturn(4L);

        final var actual = eventService.deleteEventsByTitle(eventsTitle);

        assertThat(actual.getEventsDeleted()).isEqualTo(4L);
    }

    @Test
    void findEventById() {
        final var eventId = "008e6bbc-7ad3-4a8c-a3c0-658aad072c67";
        final var event = TestDataProvider.getEventStub();
        final var expected = TestDataProvider.getEventDtoStub();

        when(elasticClient.findEventById(eventId)).thenReturn(event);
        when(eventMapper.toEventDto(event)).thenReturn(expected);

        final var actual = eventService.findEventById(eventId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindEventById_EventNotFound() {
        final var eventId = UUID.randomUUID().toString();

        when(elasticClient.findEventById(eventId)).thenReturn(null);

        assertThatThrownBy(() -> eventService.findEventById(eventId)).isInstanceOf(EventException.class);
    }

    @Test
    void testFindEventsByParameters() {
        final var findEventsDto = TestDataProvider.getFindEventsDtoStub();
        final var event = TestDataProvider.getEventStub();
        final var eventDto = TestDataProvider.getEventDtoStub();

        when(elasticClient.findEventsByParams(findEventsDto)).thenReturn(List.of(event));
        when(eventMapper.toEventDto(event)).thenReturn(eventDto);

        final var actual = eventService.findEventsByParameters(findEventsDto);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(eventDto);
    }

}
