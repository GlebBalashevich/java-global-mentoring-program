package com.epam.mentoring.library.configuration;

import java.util.Collections;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.schema.SolrPersistentEntitySchemaCreator;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories(basePackages = "com.epam.mentoring.library.repository")
public class SolrConfiguration {

    @Bean
    public SolrTemplate solrTemplate(SolrClient solrClient) {
        final var solrTemplate = new SolrTemplate(solrClient);
        solrTemplate.setSchemaCreationFeatures(
                Collections.singletonList(SolrPersistentEntitySchemaCreator.Feature.CREATE_MISSING_FIELDS));
        return solrTemplate;
    }

}
