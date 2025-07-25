package com.advantest.mcpclient;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public record ToolFunction(String name, String description, Parameters parameters) {
    public static ToolFunction of(String name, String description, Parameters parameters) {
        return new ToolFunction(name, description, parameters);
    }
}
