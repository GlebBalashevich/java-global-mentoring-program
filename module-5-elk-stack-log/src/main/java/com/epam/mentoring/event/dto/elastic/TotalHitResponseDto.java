package com.epam.mentoring.event.dto.elastic;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalHitResponseDto {

    private List<HitResponseDto> hits;

}
