package com.epam.mentoring.event.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.epam.mentoring.event.model.EventType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindEventsDto {

    private String title;

    private Instant scheduledTimeFrom;

    private EventType type;

}
