package com.epam.mentoring.event.util;

public class ErrorCode {

    private ErrorCode() {
    }

    // Elastic Service errors
    public static final String ELASTIC_SERVER_ERROR = "ESISE-0";

    public static final String ELASTIC_IO_ERROR = "ESIOE-0";

    public static final String ELASTIC_NOT_FOUND = "ESNFE-0";

    // Event Service errors
    public static final String EVENT_NOT_FOUND = "ENFE-0";

}
