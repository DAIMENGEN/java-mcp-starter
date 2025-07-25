package com.advantest.mcpclient;

import java.util.Map;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public record FunctionCall(String name, Map<String, Object> arguments) {
}
