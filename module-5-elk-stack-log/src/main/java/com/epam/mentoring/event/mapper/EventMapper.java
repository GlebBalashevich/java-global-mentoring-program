package com.epam.mentoring.event.mapper;

import org.mapstruct.Mapper;

import com.epam.mentoring.event.dto.EventDto;
import com.epam.mentoring.event.dto.UpsertEventDto;
import com.epam.mentoring.event.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event toEvent(String id, UpsertEventDto upsertEventDto);

    EventDto toEventDto(Event event);

}
