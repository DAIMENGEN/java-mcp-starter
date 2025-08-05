package com.advantest.mcpserver;

import io.modelcontextprotocol.spec.McpSchema;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public class ToolDefinition {
    private final String name;
    private final String title;
    private final String description;
    private final String inputSchema;
    private final String outputSchema;

    private ToolDefinition(Builder builder) {
        this.name = builder.name;
        this.title = builder.title;
        this.description = builder.description;
        this.inputSchema = builder.inputSchema;
        this.outputSchema = builder.outputSchema;
    }

    public String name() {
        return name;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String inputSchema() {
        return inputSchema;
    }

    public String outputSchema() {
        return outputSchema;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String title;
        private String description;
        private String inputSchema;
        private String outputSchema;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder inputSchema(String inputSchema) {
            this.inputSchema = inputSchema;
            return this;
        }

        public Builder outputSchema(String outputSchema) {
            this.outputSchema = outputSchema;
            return this;
        }

        public ToolDefinition build() {
            return new ToolDefinition(this);
        }
    }

    public McpSchema.Tool toMcpTool() {
        return McpSchema.Tool.builder()
                .name(name)
                .title(title)
                .description(description)
                .inputSchema(inputSchema)
                .outputSchema(outputSchema)
                .build();
    }
}

