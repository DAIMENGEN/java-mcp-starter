package com.advantest.mcpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public class OllamaChatModel implements ChatModel {
    private final String baseUrl;
    private final HttpClient client;
    private final String defaultModel;
    private final ChatRequest chatRequest;
    private final ObjectMapper objectMapper;

    public OllamaChatModel(String baseUrl) {
        this(baseUrl, "qwen3:8b"); // 默认模型
    }

    public OllamaChatModel(String baseUrl, String defaultModel) {
        this.baseUrl = baseUrl;
        this.defaultModel = defaultModel;
        this.chatRequest = new ChatRequest();
        this.objectMapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public OllamaChatModel tools(List<Tool> tools) {
        this.chatRequest.setTools(tools);
        return this;
    }

    public ChatResponse call(String prompt) throws IOException, InterruptedException {
        return call(prompt, this.defaultModel);
    }

    public ChatResponse call(String prompt, String model) throws IOException, InterruptedException {
        ChatMessage message = new ChatMessage(MessageType.USER, prompt);
        this.chatRequest.setModel(model);
        this.chatRequest.setStream(false); // 非流式
        this.chatRequest.setMessages(List.of(message));
        return sendChatRequest(this.chatRequest);
    }

    private ChatResponse sendChatRequest(ChatRequest chatRequest) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(chatRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), ChatResponse.class);
    }

    private Iterator<ChatResponse> sendStreamChatRequest(ChatRequest chatRequest) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(chatRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<InputStream> response = client.send(
                request, HttpResponse.BodyHandlers.ofInputStream());
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.body(), StandardCharsets.UTF_8));
        return new Iterator<>() {
            private String nextLine = null;
            private boolean finished = false;

            @Override
            public boolean hasNext() {
                if (finished) return false;
                if (nextLine != null) return true;

                try {
                    nextLine = reader.readLine();
                    if (nextLine == null) {
                        reader.close();
                        finished = true;
                        return false;
                    }
                    return true;
                } catch (IOException e) {
                    finished = true;
                    return false;
                }
            }

            @Override
            public ChatResponse next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                String line = nextLine;
                nextLine = null;

                if (line != null && !line.trim().isEmpty()) {
                    try {
                        return objectMapper.readValue(line, ChatResponse.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing JSON line: " + line, e);
                    }
                }

                return null;
            }
        };
    }

    public StreamBuilder stream() {
        return new StreamBuilder(this);
    }

    public static class StreamBuilder {
        private String model;
        private final OllamaChatModel chatModel;

        public StreamBuilder(OllamaChatModel model) {
            this.chatModel = model;
            this.model = model.defaultModel;
        }

        public StreamBuilder model(String model) {
            this.model = model;
            return this;
        }

        public Iterator<ChatResponse> call(String prompt) throws IOException, InterruptedException {
            ChatMessage message = new ChatMessage(MessageType.USER, prompt);
            this.chatModel.chatRequest.setModel(model);
            this.chatModel.chatRequest.setStream(true);
//            this.chatModel.chatRequest.setMessages(List.of(message));
            this.chatModel.chatRequest.addMessages(List.of(message));
            return this.chatModel.sendStreamChatRequest(this.chatModel.chatRequest);
        }
    }
}

