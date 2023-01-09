package com.epam.mentoring.multithreading.pcp;

import com.epam.mentoring.multithreading.pcp.model.Message;

public interface MessageBroker {

    Message consume();

    void produce(Message message);

}
