package com.epam.mentoring.multithreading.pcp.semaphore;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import lombok.extern.slf4j.Slf4j;

import com.epam.mentoring.multithreading.pcp.MessageBroker;
import com.epam.mentoring.multithreading.pcp.model.Message;

@Slf4j
public class SemaphoreSolution implements MessageBroker {

    private final Semaphore semaphore;

    private final Deque<Message> queue = new LinkedList<>();

    public SemaphoreSolution(int maxBufferSize) {
        this.semaphore = new Semaphore(maxBufferSize);
    }

    @Override
    public Message consume() {
        Message message = queue.poll();
        semaphore.release();
        if (message == null) {
            log.info("Queue is empty, nothing to consume");
        }
        return message;
    }

    @Override
    public void produce(Message message) {
        try {
            semaphore.acquire();
            queue.add(message);
        } catch (InterruptedException e) {
            log.error("Thread was interrupted");
            Thread.currentThread().interrupt();
        }
    }

}
