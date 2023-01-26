package com.epam.mentoring.event.util;

public class ElasticUtil {

    private ElasticUtil() {
    }

    public static class Field {

        private Field() {
        }

        public static final String TITLE = "title";

        public static final String SCHEDULED_TIME = "scheduledTime";

        public static final String TYPE = "type";

    }

    public static class Resource {

        private Resource() {
        }

        public static final String DOC = "_doc";

        public static final String SEARCH = "_search";

        public static final String MAPPING = "_mapping";

        public static final String DELETE_BY_QUERY = "_delete_by_query";

    }

}
