package com.advantest.mcpclient;

import java.util.Map;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public record Parameters(String type, Map<String, Object> properties) {
    public static final String DEFAULT_TYPE = "object";

    public static Parameters object(Map<String, Object> properties) {
        return new Parameters(DEFAULT_TYPE, properties);
    }

    public static Parameters of(String type, Map<String, Object> properties) {
        return new Parameters(type, properties);
    }

    public static Parameters empty() {
        return new Parameters(DEFAULT_TYPE, Map.of());
    }
}
