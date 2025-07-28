package com.advantest.mcpclient;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Create on 2025/07/28
 * Author: mengen.dai@advantest.com
 */
public class OllamaChatModel implements ChatModel {
    private final String defaultModel;
    private final OllamaApi ollamaApi;
    private final ChatRequest chatRequest;

    public OllamaChatModel(String baseUrl) {
        this(baseUrl, "qwen3:8b"); // 默认模型
    }

    public OllamaChatModel(String baseUrl, String defaultModel) {
        this.defaultModel = defaultModel;
        this.chatRequest = new ChatRequest();
        this.ollamaApi = new OllamaApi(baseUrl);
    }

    public OllamaChatModel tools(List<Tool> tools) {
        this.chatRequest.setTools(tools);
        return this;
    }

    @Override
    public ChatResponse call(String prompt) {
        return this.call(prompt, this.defaultModel);
    }

    public ChatResponse call(String prompt, String model) {
        ChatMessage message = new ChatMessage(MessageType.USER, prompt);
        this.chatRequest.setModel(model);
        this.chatRequest.setStream(false); // 非流式
        this.chatRequest.setMessages(List.of(message));
        return this.ollamaApi.chat(this.chatRequest);
    }

    @Override
    public Flux<ChatResponse> stream(String prompt) {
        return this.stream(prompt, this.defaultModel);
    }

    public Flux<ChatResponse> stream(String prompt, String model) {
        ChatMessage message = new ChatMessage(MessageType.USER, prompt);
        this.chatRequest.setModel(model);
        this.chatRequest.setStream(true); // 非流式
        this.chatRequest.setMessages(List.of(message));
        return this.ollamaApi.streamingChat(this.chatRequest);
    }
}
