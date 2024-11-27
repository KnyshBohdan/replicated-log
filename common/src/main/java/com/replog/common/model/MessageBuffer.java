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

    public int getSize(){
        return this.messages.size();
    }

    public List<Message> getMessages() {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message == null) {
                break;
            }
            result.add(message);
        }
        return Collections.unmodifiableList(result);
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
