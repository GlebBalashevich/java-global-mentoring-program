package com.epam.mentoring.multithreading.pcp;

import java.util.List;

import com.epam.mentoring.multithreading.pcp.model.Message;

public class TestDataProvider {

    private TestDataProvider() {
    }

    public static List<Message> getMessagesStub() {
        Message message1 = Message.builder().id(1L).payload("payload1").build();
        Message message2 = Message.builder().id(2L).payload("payload2").build();
        Message message3 = Message.builder().id(3L).payload("payload3").build();
        Message message4 = Message.builder().id(4L).payload("payload4").build();
        Message message5 = Message.builder().id(5L).payload("payload5").build();
        Message message6 = Message.builder().id(6L).payload("payload6").build();
        Message message7 = Message.builder().id(7L).payload("payload7").build();
        Message message8 = Message.builder().id(8L).payload("payload8").build();
        return List.of(message1, message2, message3, message4, message5, message6, message7, message8);
    }

}
