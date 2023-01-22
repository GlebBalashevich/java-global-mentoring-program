package com.epam.mentoring.library.configuration;

import java.io.File;

import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EpubBookReaderConfiguration {

    @Value("${books.directory}")
    private String booksDirectory;

    @Bean
    File fileDirectory() {
        return new File(booksDirectory);
    }

    @Bean
    EpubReader epubReader() {
        return new EpubReader();
    }

}
