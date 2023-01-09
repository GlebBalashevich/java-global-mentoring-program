package com.epam.mentoring.multithreading.pcp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {

    private Long id;

    private String payload;

}
