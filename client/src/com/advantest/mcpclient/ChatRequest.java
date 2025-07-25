package com.advantest.mcpclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public class ChatRequest {
    private String model;
    private boolean stream;
    private List<Tool> tools;
    private List<ChatMessage> messages;

    public ChatRequest() {
    }

    public ChatRequest(String model, boolean stream, List<Tool> tools, List<ChatMessage> messages) {
        this.model = model;
        this.stream = stream;
        this.tools = tools;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(ChatMessage message) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(message);
    }

    public void addMessages(List<ChatMessage> messages) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.addAll(messages);
    }
}
