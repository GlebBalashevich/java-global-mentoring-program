package com.epam.mentoring.event.controller;

import java.time.Instant;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.mentoring.event.dto.DeleteEventsResponseDto;
import com.epam.mentoring.event.dto.EventDto;
import com.epam.mentoring.event.dto.FindEventsDto;
import com.epam.mentoring.event.dto.UpdateEventDto;
import com.epam.mentoring.event.dto.UpsertEventDto;
import com.epam.mentoring.event.model.EventType;
import com.epam.mentoring.event.service.EventService;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto saveEvent(@RequestBody @Validated UpsertEventDto upsertEventDto) {
        log.debug("Requested save new event: {}", upsertEventDto);
        return eventService.saveEvent(upsertEventDto);
    }

    @PutMapping("/{id}")
    public EventDto replaceEvent(@PathVariable String id, @RequestBody @Validated UpsertEventDto upsertEventDto) {
        log.debug("Requested replace event with id: {}", id);
        return eventService.replaceEvent(id, upsertEventDto);

    }

    @PatchMapping("/{id}")
    public EventDto updateEvent(@PathVariable String id, @RequestBody @Validated UpdateEventDto updateEventDto) {
        log.debug("Requested update event with id: {}", id);
        return eventService.updateEvent(id, updateEventDto);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable String id) {
        log.debug("Requested delete event with id: {}", id);
        eventService.deleteEventById(id);
    }

    @DeleteMapping("/title/{title}")
    public DeleteEventsResponseDto deleteEventsByTitle(@PathVariable String title) {
        log.debug("Requested delete events with title: {}", title);
        return eventService.deleteEventsByTitle(title);
    }

    @GetMapping("/{id}")
    public EventDto findEventById(@PathVariable String id) {
        log.debug("Requested search event by id: {}", id);
        return eventService.findEventById(id);
    }

    @GetMapping
    public List<EventDto> findEvensByParameters(@RequestParam(required = false) String title,
            @RequestParam(required = false) Instant scheduledTimeFrom,
            @RequestParam(required = false) EventType type) {
        final var findEventsDto = FindEventsDto.builder().title(title).scheduledTimeFrom(scheduledTimeFrom).type(type)
                .build();
        log.debug("Requested search events by parameters: {}", findEventsDto);
        return eventService.findEventsByParameters(findEventsDto);
    }

}
