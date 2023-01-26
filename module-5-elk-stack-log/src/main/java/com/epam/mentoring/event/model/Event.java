package com.epam.mentoring.event.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Event {

    private String id;

    private String title;

    private EventType type;

    private Instant scheduledTime;

    private String description;

    private List<String> subTopics;

}
