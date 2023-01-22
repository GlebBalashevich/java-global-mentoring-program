package com.epam.mentoring.library.reader;

import java.io.File;

import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ MockitoExtension.class })
class EpubBookReaderTest {

    @Test
    void testReadEpubBooks() {
        EpubReader epubReader = new EpubReader();
        File file = new File("src/test/resources/books/");
        EpubBookReader epubBookReader = new EpubBookReader(epubReader, file);

        final var actual = epubBookReader.readEpubBooks();

        assertThat(actual).hasSize(1);
        final var actualBook = actual.get(0);
        assertThat(actualBook.getMetadata().getFirstTitle())
                .isEqualTo("Fundamental Accessibility Tests: Basic Functionality");
        assertThat(actualBook.getMetadata().getAuthors().get(0).getFirstname()).isEqualTo("DAISY");
        assertThat(actualBook.getMetadata().getAuthors().get(0).getLastname()).isEqualTo("Consortium");
        assertThat(actualBook.getMetadata().getLanguage()).isEqualTo("en");
        assertThat(actualBook.getSpine()).isNotNull();
    }

    @Test
    void testReadEpubBooks_EmptyDirectory() {
        EpubReader epubReader = new EpubReader();
        File file = new File("empty directory");
        EpubBookReader epubBookReader = new EpubBookReader(epubReader, file);

        final var actual = epubBookReader.readEpubBooks();

        assertThat(actual).isEmpty();
    }

}
