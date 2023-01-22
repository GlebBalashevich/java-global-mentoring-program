package com.epam.mentoring.library.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SolrDocument(collection = "books")
public class Book {

    @Id
    private String id;

    @Indexed(name = "title", type = "string")
    private String title;

    @Indexed(name = "authors", type = "strings")
    private List<String> authors;

    @Indexed(name = "content", type = "text_general")
    private String content;

    @Indexed(name = "language", type = "string")
    private String language;

}
