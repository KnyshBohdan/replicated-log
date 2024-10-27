package com.replog.common.model;

import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class MessageBuffer {
    private List<Message> messages;
    public MessageBuffer() {
        this.messages = new ArrayList<Message>();
    }

    public List<Message> getMessages(){
        return Collections.unmodifiableList(messages);
    }

    public void add(Message message) {
        Comparator<Message> messageComparator = new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return m1.getEndlessCounterState().compareTo(m2.getEndlessCounterState());
            }
        };

        int index = Collections.binarySearch(this.messages, message, messageComparator);

        if (index < 0) {
            index = -index - 1;
        }

        this.messages.add(index, message);
    }
}
