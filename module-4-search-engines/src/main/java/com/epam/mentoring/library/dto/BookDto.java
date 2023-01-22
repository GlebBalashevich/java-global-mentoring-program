package com.epam.mentoring.library.dto;

import java.util.List;

import lombok.Data;

@Data
public class BookDto {

    private String id;

    private String title;

    private List<String> authors;

    private String content;

    private String language;

}
