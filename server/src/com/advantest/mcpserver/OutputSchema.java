package com.advantest.mcpserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 * Refer to <a href="https://modelcontextprotocol.io/specification/2025-06-18/server/tools">Tools</a>
 */
public final class OutputSchema {

    private final String type;
    private final Map<String, Property> properties;
    private final String[] required;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private OutputSchema(Builder builder) {
        this.type = builder.type;
        this.properties = Map.copyOf(builder.properties);
        this.required = builder.required == null ? null : builder.required.clone();
    }

    public String getType() {
        return type;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public String[] getRequired() {
        return required;
    }

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type;
        private final Map<String, Property> properties = new LinkedHashMap<>();
        private String[] required;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder addProperty(String key, Property property) {
            if (key == null || property == null) {
                throw new IllegalArgumentException("key and property must not be null");
            }
            this.properties.put(key, property);
            return this;
        }

        public Builder properties(Map<String, Property> properties) {
            this.properties.clear();
            if (properties != null) {
                this.properties.putAll(properties);
            }
            return this;
        }

        public Builder required(String... required) {
            this.required = required;
            return this;
        }

        public OutputSchema build() {
            if (type == null || type.isEmpty()) {
                type = "object";
//                throw new IllegalStateException("type must be set");
            }
            return new OutputSchema(this);
        }
    }
}

