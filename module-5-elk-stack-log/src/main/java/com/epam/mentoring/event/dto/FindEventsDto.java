package com.epam.mentoring.event.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

import com.epam.mentoring.event.model.EventType;

@Data
@Builder
public class FindEventsDto {

    private String title;

    private Instant scheduledTimeFrom;

    private EventType type;

}
