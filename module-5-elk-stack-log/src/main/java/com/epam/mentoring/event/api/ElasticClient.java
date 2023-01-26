package com.epam.mentoring.event.api;

import java.util.List;

import com.epam.mentoring.event.dto.FindEventsDto;
import com.epam.mentoring.event.model.Event;

public interface ElasticClient {

    void createIndex();

    void updateIndexMappings();

    String upsertEvent(Event event);

    Event findEventById(String id);

    void deleteEventById(String id);

    Long deleteEventsByTitle(String title);

    List<Event> findEventsByParams(FindEventsDto findEvents);

}
