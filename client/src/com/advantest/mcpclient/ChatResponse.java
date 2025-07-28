package com.advantest.mcpclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.ZonedDateTime;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public record ChatResponse(
        String model,

        @JsonProperty("created_at")
        ZonedDateTime createdAt,

        AssistantMessage message,

        @JsonProperty("done_reason")
        String doneReason,

        boolean done,

        @JsonProperty("total_duration")
        Long totalDuration,

        @JsonProperty("load_duration")
        Long loadDuration,

        @JsonProperty("prompt_eval_count")
        Integer promptEvalCount,

        @JsonProperty("prompt_eval_duration")
        Long promptEvalDuration,

        @JsonProperty("eval_count")
        Integer evalCount,

        @JsonProperty("eval_duration")
        Long evalDuration
) {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public boolean requiresToolExecution() {
        if (this.message() != null && this.message().toolCalls() != null) {
            return !this.message().toolCalls().isEmpty();
        }
        return false;
    }

    public static ChatResponse fromJson(String json) {
        try {
            return objectMapper.readValue(json, ChatResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON: ", e);
        }
    }
}
