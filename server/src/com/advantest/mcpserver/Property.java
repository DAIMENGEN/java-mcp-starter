package com.advantest.mcpserver;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public record Property(
        String type,
        String description
) {
    public static Property of(String type) {
        return new Property(type, null);
    }

    public static Property of(String type, String description) {
        return new Property(type, description);
    }
}
