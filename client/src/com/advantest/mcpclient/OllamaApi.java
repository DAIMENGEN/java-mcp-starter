package com.advantest.mcpclient;

import io.modelcontextprotocol.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Create on 2025/07/28
 * Author: mengen.dai@advantest.com
 */
public final class OllamaApi {
    private final String baseUrl;
    private final HttpClient client;

    private HttpRequest buildChatRequest(ChatRequest chatRequest) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(chatRequest.toJson()))
                .build();
    }

    public OllamaApi(String baseUrl) {
        Assert.hasText(baseUrl, "baseUrl cannot be null or empty");
        this.baseUrl = baseUrl;
        this.client = HttpClient.newHttpClient();
    }

    public ChatResponse chat(ChatRequest chatRequest) {
        HttpRequest request = this.buildChatRequest(chatRequest);
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ChatResponse.fromJson(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Flux<ChatResponse> streamingChat(ChatRequest chatRequest) {
        HttpRequest request = this.buildChatRequest(chatRequest);
        CompletableFuture<HttpResponse<InputStream>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream());
        return Mono.fromFuture(futureResponse)
                .flatMapMany(response -> {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8));
                    return Flux.<ChatResponse>create(sink -> {
                        try (reader) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.trim().isEmpty()) continue;
                                ChatResponse chatResponse = ChatResponse.fromJson(line);
                                sink.next(chatResponse);
                            }
                            sink.complete();
                        } catch (Exception e) {
                            sink.error(e);
                        }
                    }).subscribeOn(Schedulers.boundedElastic());
                });
    }
}
