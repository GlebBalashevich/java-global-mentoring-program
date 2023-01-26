package com.epam.mentoring.event.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

@Configuration
public class ElasticConfiguration extends ElasticsearchRestClientAutoConfiguration {

    @Value("${indexMappings.directory}")
    private String indexMappingsDirectory;

    @Bean
    public RestClient restClient(ElasticsearchProperties elasticsearchProperties) {
        return RestClient.builder(HttpHost.create(elasticsearchProperties.getUris().get(0))).build();
    }

    @Bean
    public ElasticsearchClient apiClient(RestClient restClient, JacksonJsonpMapper jsonpMapper) {
        final var transport = new RestClientTransport(restClient, jsonpMapper);
        return new ElasticsearchClient(transport);
    }

    @Bean
    public Map<String, String> indexMappings(ResourceLoader resourceLoader) throws IOException {
        final var resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                .getResources(indexMappingsDirectory);
        Map<String, String> indexMappings = new HashMap<>();
        for (Resource resource : resources) {
            final var indexName = resource.getFile().getName().split("\\.")[0];
            final var indexMapping = Files.readString(resource.getFile().toPath());
            indexMappings.put(indexName, indexMapping);
        }
        return indexMappings;
    }

}
