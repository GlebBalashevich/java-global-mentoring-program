package com.epam.mentoring.multithreading.pcp.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.extern.slf4j.Slf4j;

import com.epam.mentoring.multithreading.pcp.MessageBroker;
import com.epam.mentoring.multithreading.pcp.model.Message;

@Slf4j
public class BlockingQueueSolution implements MessageBroker {

    private final BlockingQueue<Message> queue;

    public BlockingQueueSolution(int maxBufferSize) {
        this.queue = new LinkedBlockingQueue<>(maxBufferSize);
    }

    @Override
    public Message consume() {
        Message message = queue.poll();
        if (message == null) {
            log.info("Queue is empty, nothing to consume");
        }
        return message;
    }

    @Override
    public void produce(Message message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            log.error("Thread was interrupted");
            Thread.currentThread().interrupt();
        }
    }

}
