package com.epam.mentoring.event.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.epam.mentoring.event.api.ElasticClient;
import com.epam.mentoring.event.dto.DeleteEventsResponseDto;
import com.epam.mentoring.event.dto.EventDto;
import com.epam.mentoring.event.dto.FindEventsDto;
import com.epam.mentoring.event.dto.UpdateEventDto;
import com.epam.mentoring.event.dto.UpsertEventDto;
import com.epam.mentoring.event.exception.EventException;
import com.epam.mentoring.event.mapper.EventMapper;
import com.epam.mentoring.event.model.Event;
import com.epam.mentoring.event.util.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final ElasticClient elasticClient;

    private final EventMapper eventMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeIndex() {
        log.info("Initializing index for events...");
        elasticClient.createIndex();
        log.info("Index was successfully initialized, updating index mappings...");
        elasticClient.updateIndexMappings();
        log.info("Index mapping was successfully updated");
    }

    public EventDto saveEvent(UpsertEventDto upsertEventDto) {
        final var event = eventMapper.toEvent(UUID.randomUUID().toString(), upsertEventDto);
        elasticClient.upsertEvent(event);
        return eventMapper.toEventDto(event);
    }

    public EventDto replaceEvent(String id, UpsertEventDto upsertEventDto) {
        if (elasticClient.findEventById(id) == null) {
            throw notFoundException(id);
        }
        final var replacingEvent = eventMapper.toEvent(id, upsertEventDto);
        elasticClient.upsertEvent(replacingEvent);
        return eventMapper.toEventDto(replacingEvent);
    }

    public EventDto updateEvent(String id, UpdateEventDto updateEventDto) {
        final var existingEvent = Optional.ofNullable(elasticClient.findEventById(id))
                .orElseThrow(() -> notFoundException(id));
        final var updatingEvent = mergeEvents(existingEvent, updateEventDto);
        elasticClient.upsertEvent(updatingEvent);
        return eventMapper.toEventDto(updatingEvent);
    }

    public void deleteEventById(String id) {
        if (elasticClient.findEventById(id) == null) {
            throw notFoundException(id);
        }
        elasticClient.deleteEventById(id);
    }

    public DeleteEventsResponseDto deleteEventsByTitle(String title) {
        return DeleteEventsResponseDto.builder().eventsDeleted(elasticClient.deleteEventsByTitle(title)).build();
    }

    public EventDto findEventById(String id) {
        return Optional.ofNullable(elasticClient.findEventById(id))
                .map(eventMapper::toEventDto)
                .orElseThrow(() -> notFoundException(id));
    }

    public List<EventDto> findEventsByParameters(FindEventsDto findEventsDto) {
        return elasticClient.findEventsByParams(findEventsDto).stream()
                .map(eventMapper::toEventDto).toList();
    }

    private Event mergeEvents(Event existingEvent, UpdateEventDto updateEventDto) {
        final var eventBuilder = existingEvent.toBuilder();
        if (StringUtils.hasText(updateEventDto.getTitle())) {
            eventBuilder.title(updateEventDto.getTitle());
        }
        if (updateEventDto.getType() != null) {
            eventBuilder.type(updateEventDto.getType());
        }
        if (updateEventDto.getScheduledTime() != null) {
            eventBuilder.scheduledTime(updateEventDto.getScheduledTime());
        }
        if (StringUtils.hasText(updateEventDto.getDescription())) {
            eventBuilder.description(updateEventDto.getDescription());
        }
        if (!CollectionUtils.isEmpty(updateEventDto.getSubTopics())) {
            eventBuilder.subTopics(updateEventDto.getSubTopics());
        }
        return eventBuilder.build();
    }

    private EventException notFoundException(String id) {
        final var message = String.format("Error Event with id %s not found", id);
        log.error(message);
        return new EventException(message, HttpStatus.NOT_FOUND, ErrorCode.EVENT_NOT_FOUND);
    }

}
