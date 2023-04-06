package com.epam.mentoring.event.dto.elastic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteEventsByTitleResponseDto {

    private Long deleted;

}
