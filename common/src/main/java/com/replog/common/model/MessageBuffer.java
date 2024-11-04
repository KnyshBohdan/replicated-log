package com.replog.common.model;

import com.replog.common.counter.EndlessCounter;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class MessageBuffer {
    private List<Message> messages;
    private EndlessCounter endlessCounter;

    public MessageBuffer() {
        this.messages = new ArrayList<Message>();
        this.endlessCounter = new EndlessCounter();
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void add(Message message) {
        // Just bruteforce implementation, can be make much faster
        EndlessCounterState messageState = message.getEndlessCounterState();
        EndlessCounterState currentState = new EndlessCounterState();

        int index = 0;
        while (currentState.compareTo(messageState) < 0) {
            if (index < messages.size()) {
                index++;
            } else {
                messages.add(index, null);
                index++;
            }
            endlessCounter.increment(currentState);
        }

        if (index < messages.size()) {
            messages.set(index, message);
        } else {
            messages.add(index, message);
        }
    }
}
