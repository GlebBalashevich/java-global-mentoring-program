package com.epam.mentoring.event.dto;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Data;

import com.epam.mentoring.event.model.EventType;

@Data
@Builder
public class EventDto {

    private String id;

    private String title;

    private EventType type;

    private Instant scheduledTime;

    private String description;

    private List<String> subTopics;

}
