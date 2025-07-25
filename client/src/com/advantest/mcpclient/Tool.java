package com.advantest.mcpclient;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public record Tool(String type, ToolFunction function) {

    public static final String DEFAULT_TYPE = "function";

    public Tool(ToolFunction function) {
        this(DEFAULT_TYPE, function);
    }

    public static Tool of(String type, ToolFunction function) {
        return new Tool(type, function);
    }

    public static Tool function(ToolFunction function) {
        return new Tool(function);
    }
}
