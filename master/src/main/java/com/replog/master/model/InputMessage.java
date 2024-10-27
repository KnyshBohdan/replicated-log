package com.replog.master.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InputMessage {
    private String content;
    private Integer writeConcern;

    @JsonCreator
    public InputMessage(@JsonProperty("content") String content,
                        @JsonProperty("writeConcern") Integer writeConcern) {
        this.content = content;
        this.writeConcern = writeConcern;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Integer getWriteConcern() {
        return writeConcern;
    }
    public void setWriteConcern(Integer writeConcern) {
        this.writeConcern = writeConcern;
    }
}
