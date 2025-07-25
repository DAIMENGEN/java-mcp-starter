package com.advantest.mcpclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageType {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system"),
    TOOL("tool");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MessageType fromValue(String value) {
        for (MessageType messageType : values()) {
            if (messageType.value.equalsIgnoreCase(value)) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Invalid MessageType: " + value);
    }
}
