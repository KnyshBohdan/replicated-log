package com.replog.common.model;

public class Message {
    private String content;
    private EndlessCounterState endlessCounterState;

    public Message() {}

    public Message(String content) {
        this.content = content;
        this.endlessCounterState = new EndlessCounterState();
    }

    public String getContent() {
        return content;
    }
    public EndlessCounterState getEndlessCounterState() {
        return endlessCounterState;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setEndlessCounterState(EndlessCounterState endlessCounterState) {
        this.endlessCounterState = endlessCounterState;
    }
}
