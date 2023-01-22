package com.epam.mentoring.library.util;

public class ErrorCode {

    private ErrorCode() {
    }

    // EPUB Reader errors
    public static final String EPUB_SCANNING_ERROR = "EIOE-0";

    // Book Service errors
    public static final String BOOK_NOT_FOUND_ERROR = "BNFE-0";

    public static final String BOOK_BAD_REQUEST_ERROR = "BBRE-0";

    // Solr Server errors
    public static final String SOLR_SUGGESTION_ERROR = "SISE-0";

}
