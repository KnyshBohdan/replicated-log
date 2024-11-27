package com.replog.common.counter;

import com.replog.common.counter.EndlessCounter;
import com.replog.common.model.EndlessCounterState;
import com.replog.common.model.Message;
import com.replog.common.model.MessageBuffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class MessageBufferTest {
    private EndlessCounter counter;

    @BeforeEach
    public void setUp() {
        counter = new EndlessCounter();
    }

    @Test
    public void testIncrement() {
        EndlessCounterState state = new EndlessCounterState();
        String content1 = "First";
        Message msg1 = new Message(content1);
        EndlessCounterState state1 = new EndlessCounterState(state);
        msg1.setEndlessCounterState(state1);

        counter.increment(state);

        String content2 = "Second";
        EndlessCounterState state2 = new EndlessCounterState(state);
        Message msg2 = new Message(content2);
        msg2.setEndlessCounterState(state2);

        counter.increment(state);

        String content3 = "Third";
        Message msg3 = new Message(content3);
        EndlessCounterState state3 = new EndlessCounterState(state);
        msg3.setEndlessCounterState(state3);

        counter.increment(state);

        String content4 = "Fourth";
        Message msg4 = new Message(content4);
        EndlessCounterState state4 = new EndlessCounterState(state);
        msg4.setEndlessCounterState(state4);

        counter.increment(state);

        String content5 = "Fifth";
        Message msg5 = new Message(content5);
        EndlessCounterState state5 = new EndlessCounterState(state);
        msg5.setEndlessCounterState(state5);

        MessageBuffer buffer = new MessageBuffer();

        buffer.add(msg4);
        buffer.add(msg1);

        List<Message> msgs = buffer.getMessages();
        Assertions.assertEquals(1, msgs.size());
        Assertions.assertEquals(4, buffer.getSize());

        buffer.add(msg3);
        buffer.add(msg2);
        buffer.add(msg5);

        msgs = buffer.getMessages();

        Assertions.assertEquals("First", msgs.get(0).getContent());
        Assertions.assertEquals("Second", msgs.get(1).getContent());
        Assertions.assertEquals("Third", msgs.get(2).getContent());
        Assertions.assertEquals("Fourth", msgs.get(3).getContent());
        Assertions.assertEquals("Fifth", msgs.get(4).getContent());
        Assertions.assertEquals(5, msgs.size());
    }
}
