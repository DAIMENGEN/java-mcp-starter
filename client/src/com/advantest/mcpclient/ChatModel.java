package com.advantest.mcpclient;

import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public interface ChatModel {

    ChatResponse call(String prompt) throws IOException, InterruptedException;

    Flux<ChatResponse> stream(String prompt);
}
