package com.epam.mentoring.event.dto;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.Future;

import lombok.Data;

import com.epam.mentoring.event.model.EventType;

@Data
public class UpdateEventDto {

    private String title;

    private EventType type;

    @Future
    private Instant scheduledTime;

    private String description;

    private List<String> subTopics;

}
