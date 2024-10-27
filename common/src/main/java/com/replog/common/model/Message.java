package com.replog.common.model;

public class Message {
    private String content;
    private String masterID;
    private long masterTimestamp;
    private double msgIDReal;
    private double msgIDImg;

    public Message() {}

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
