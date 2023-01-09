package com.epam.mentoring.multithreading.pcp.semaphore;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.epam.mentoring.multithreading.pcp.MessageBroker;
import com.epam.mentoring.multithreading.pcp.TestDataProvider;
import com.epam.mentoring.multithreading.pcp.model.Message;

class SemaphoreSolutionTest {

    private MessageBroker messageBroker;

    @BeforeEach
    void init() {
        messageBroker = new SemaphoreSolution(5);
    }

    @Test
    void testConsumeProduceProblem() throws ExecutionException, InterruptedException {
        List<Message> messages = TestDataProvider.getMessagesStub();
        Callable<Integer> callable = () -> {
            int numberOfMessages = 0;
            while (numberOfMessages < messages.size()) {
                if (messageBroker.consume() != null) {
                    numberOfMessages++;
                }
            }
            return numberOfMessages;
        };

        Future<Integer> consumedMessages = Executors.newCachedThreadPool().submit(callable);
        messages.parallelStream().forEach(message -> messageBroker.produce(message));
        Integer numberOfConsumedMessages = consumedMessages.get();

        Assertions.assertThat(numberOfConsumedMessages).isNotNull().isEqualTo(messages.size());
        Assertions.assertThat(messageBroker.consume()).isNull();
    }

}
