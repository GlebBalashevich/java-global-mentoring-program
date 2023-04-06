package com.epam.mentoring.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteEventsResponseDto {

    private Long eventsDeleted;

}
