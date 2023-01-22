package com.epam.mentoring.library.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.epam.mentoring.library.exception.EpubFileReaderException;
import com.epam.mentoring.library.util.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class EpubBookReader {

    private final EpubReader epubReader;

    private final File file;

    public List<Book> readEpubBooks() {
        List<Book> books = Collections.emptyList();
        if (file.isDirectory() && file.listFiles() != null) {
            List<File> fileList = Arrays.asList(Objects.requireNonNull(file.listFiles()));
            books = fileList.stream().filter(File::isFile)
                    .map(this::readBook)
                    .toList();
        }
        return books;
    }

    private Book readBook(File file) {
        Book book;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            book = epubReader.readEpub(fileInputStream);
        } catch (IOException e) {
            log.error("File:{} not found", file.getName(), e);
            throw new EpubFileReaderException("File not found", e, HttpStatus.NOT_FOUND,
                    ErrorCode.EPUB_SCANNING_ERROR);
        }
        return book;
    }

}
