package com.advantest.mcpclient;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    public boolean requiresToolExecution() {
        if (this.message() != null && this.message().toolCalls() != null) {
            return !this.message().toolCalls().isEmpty();
        }
        return false;
    }
}
