package com.epam.mentoring.event;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;

import com.epam.mentoring.event.dto.EventDto;
import com.epam.mentoring.event.dto.FindEventsDto;
import com.epam.mentoring.event.dto.UpdateEventDto;
import com.epam.mentoring.event.dto.UpsertEventDto;
import com.epam.mentoring.event.model.Event;
import com.epam.mentoring.event.model.EventType;

public class TestDataProvider {

    private TestDataProvider() {
    }

    private static final String INDEX = "events";

    public static UpsertEventDto getUpsertEventDtoStub() {
        final var upsertEventDto = new UpsertEventDto();
        upsertEventDto.setTitle("Test event");
        upsertEventDto.setType(EventType.WORKSHOP);
        upsertEventDto.setDescription("Event description");
        upsertEventDto.setScheduledTime(Instant.parse("2023-01-14T16:30:00Z"));
        upsertEventDto.setSubTopics(List.of("sub topic 1", "subtopic2"));
        return upsertEventDto;
    }

    public static UpdateEventDto getUpdateEventDto() {
        final var updateEventDto = new UpdateEventDto();
        updateEventDto.setTitle("New Event");
        updateEventDto.setType(EventType.TECH_TALK);
        updateEventDto.setSubTopics(Collections.emptyList());
        return updateEventDto;
    }

    public static FindEventsDto getFindEventsDtoStub() {
        final var findEventsDto = new FindEventsDto();
        findEventsDto.setType(EventType.WORKSHOP);
        findEventsDto.setTitle("Test event");
        findEventsDto.setScheduledTimeFrom(Instant.parse("2023-01-13T12:00:00Z"));
        return findEventsDto;
    }

    public static Event getEventStub() {
        return Event.builder()
                .id("008e6bbc-7ad3-4a8c-a3c0-658aad072c67")
                .title("Test event")
                .type(EventType.WORKSHOP)
                .description("Event description")
                .scheduledTime(Instant.parse("2023-01-14T16:30:00Z"))
                .subTopics(List.of("sub topic 1", "subtopic2")).build();
    }

    public static EventDto getEventDtoStub() {
        return EventDto.builder()
                .id("008e6bbc-7ad3-4a8c-a3c0-658aad072c67")
                .title("Test event")
                .type(EventType.WORKSHOP)
                .description("Event description")
                .scheduledTime(Instant.parse("2023-01-14T16:30:00Z"))
                .subTopics(List.of("sub topic 1", "subtopic2")).build();
    }

    public static CreateIndexResponse getCreateIndexResponseStub(boolean acknowledge) {
        return new CreateIndexResponse.Builder()
                .index(INDEX)
                .shardsAcknowledged(false)
                .acknowledged(acknowledge).build();
    }

    public static PutMappingResponse getPutMappingResponseStub(boolean acknowledge) {
        return new PutMappingResponse.Builder()
                .acknowledged(acknowledge).build();
    }

    public static IndexResponse getIndexResponseStub() {
        return new IndexResponse.Builder()
                .index(INDEX)
                .id("008e6bbc-7ad3-4a8c-a3c0-658aad072c67")
                .result(Result.Created)
                .primaryTerm(1L)
                .seqNo(1L)
                .version(1L)
                .shards(getShardStatisticsStub())
                .build();
    }

    public static GetResponse<Event> getGetResponseStub(Event event) {
        return new GetResponse.Builder<Event>()
                .id(event.getId())
                .index(INDEX)
                .primaryTerm(1L)
                .seqNo(1L)
                .found(true)
                .source(event)
                .version(1L)
                .build();
    }

    public static SearchResponse<Event> getSearchResponseStub(Event event) {
        return new SearchResponse.Builder<Event>()
                .took(10L)
                .timedOut(false)
                .shards(getShardStatisticsStub())
                .hits(getHitsMetadataStub(event))
                .build();
    }

    public static DeleteResponse getDeleteResponseStub() {
        return new DeleteResponse.Builder()
                .id(UUID.randomUUID().toString())
                .seqNo(1L)
                .index(INDEX)
                .primaryTerm(1L)
                .shards(getShardStatisticsStub())
                .result(Result.Deleted)
                .version(1L)
                .build();
    }

    public static DeleteByQueryResponse getDeleteByQueryResponseStub() {
        return new DeleteByQueryResponse.Builder()
                .deleted(2L)
                .took(10L)
                .throttledMillis(1L)
                .throttledUntilMillis(2L)
                .total(2L)
                .timedOut(false)
                .build();
    }

    public static InputStream getCreateIndexContentStreamStub(boolean acknowledged) {
        final var createIndexContent = "{" +
                "\"acknowledged\": \"" + acknowledged + "\",\n" +
                "\"shards_acknowledged\": \"true\",\n" +
                "\"index\": \"events\"\n}";
        return new ByteArrayInputStream(createIndexContent.getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream getUpdateMappingsContentStreamStub(boolean acknowledged) {
        final var updateMappingsContent = "{" +
                "\"acknowledged\": \"" + acknowledged + "\"}";
        return new ByteArrayInputStream(updateMappingsContent.getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream getIndexEventContentStreamStub(String id) {
        final var indexEventContent = "{" +
                "\"_id\": \"" + id + "\",\n" +
                "\"result\": \"created\",\n" +
                "\"_version\": \"1\"\n}";
        return new ByteArrayInputStream(indexEventContent.getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream getFindEventByIdResponseContentStub(Event event) {
        final var findEventResponseContent = "{ \n" +
                "\"_id\": \"" + event.getId() + "\",\n" +
                "\"found\": \"true\",\n" +
                "\"_source\": {\n" +
                getEventContentStub(event) +
                "\n}}";
        return new ByteArrayInputStream(findEventResponseContent.getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream getFindEventsByParametersResponseContentStub(Event event) {
        final var findEventsByParametersContent = "{\n" +
                "    \"hits\": {\n" +
                "        \"hits\": [\n" +
                "            {\n" +
                "                \"_index\": \"events\",\n" +
                "                \"_id\": \"" + event.getId() + "\",\n" +
                "                \"_score\": 1.0,\n" +
                "                \"_source\": {\n" +
                getEventContentStub(event) +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        return new ByteArrayInputStream(findEventsByParametersContent.getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream getDeleteByIdResponseContentStreamStub(String id) {
        final var deleteEventContent = "{" +
                "\"_id\": \"" + id + "\",\n" +
                "\"result\": \"deleted\"\n}";
        return new ByteArrayInputStream(deleteEventContent.getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream getDeleteByQueryResponseContentStreamStub() {
        final var deleteEventContent = "{" +
                "\"deleted\": \"2\"\n}";
        return new ByteArrayInputStream(deleteEventContent.getBytes(StandardCharsets.UTF_8));
    }

    private static String getEventContentStub(Event event) {
        return "   \"id\": \"" + event.getId() + "\",\n" +
                "   \"type\": \"" + event.getType() + "\",\n" +
                "   \"title\": \"" + event.getTitle() + "\",\n" +
                "   \"scheduledTime\": \"" + event.getScheduledTime().toString() + "\",\n" +
                "   \"description\": \"" + event.getDescription() + "\",\n" +
                "   \"subTopics\": [\"" + event.getSubTopics().get(0) + "\", \"" + event.getSubTopics().get(1) + "\"]";
    }

    private static HitsMetadata<Event> getHitsMetadataStub(Event event) {
        List<Hit<Event>> hits = new ArrayList<>();
        hits.add(new Hit.Builder<Event>().index(INDEX).id(event.getId()).source(event).seqNo(1L).build());
        return new HitsMetadata.Builder<Event>()
                .hits(hits).build();
    }

    private static ShardStatistics getShardStatisticsStub() {
        return new ShardStatistics.Builder().failed(0).successful(0).total(1).build();
    }

}
