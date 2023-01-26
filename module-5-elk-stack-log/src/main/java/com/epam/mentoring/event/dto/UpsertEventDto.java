package com.epam.mentoring.event.dto;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

import com.epam.mentoring.event.model.EventType;

@Data
public class UpsertEventDto {

    @NotBlank
    private String title;

    @NotNull
    private EventType type;

    @NotNull
    @Future
    private Instant scheduledTime;

    private String description;

    private List<String> subTopics;

}
