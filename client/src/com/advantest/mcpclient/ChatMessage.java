package com.advantest.mcpclient;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public record ChatMessage(
        MessageType role,
        String content
) {}
