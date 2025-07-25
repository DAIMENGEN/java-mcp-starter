package com.advantest.mcpclient;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public record AssistantMessage(
        MessageType role,

        String content,

        @JsonProperty("tool_calls")
        List<ToolCall> toolCalls
) {}
