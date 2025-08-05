package com.advantest.mcpserver.utils;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public class StringUtils {

    public static boolean hasText(String str) {
        return str != null && !str.isBlank();
    }
}
