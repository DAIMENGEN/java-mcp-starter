package com.advantest.mcpserver.tool.support;

import com.advantest.mcpserver.tool.ToolCallback;
import com.advantest.mcpserver.tool.annotation.Tool;
import io.modelcontextprotocol.util.Assert;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public final class ToolUtils {

    private ToolUtils() {
    }

    public static String getToolName(Method method) {
        Assert.notNull(method, "method cannot be null");
        Tool tool = method.getAnnotation(Tool.class);
        if (tool == null) {
            return method.getName();
        } else {
            String name = tool.name();
            if (name != null && !name.isBlank()) {
                return name;
            } else {
                return method.getName();
            }
        }
    }

    public static String getToolDescription(Method method) {
        Assert.notNull(method, "method cannot be null");
        Tool tool = method.getAnnotation(Tool.class);
        if (tool == null) {
            return method.getName();
        } else {
            String description = tool.description();
            if (description != null && !description.isBlank()) {
                return description;
            } else {
                return method.getName();
            }
        }
    }

    public static List<String> getDuplicateToolNames(List<ToolCallback> toolCallbacks) {
        return toolCallbacks.stream()
                .map(callback -> callback.getToolDefinition().name().toLowerCase())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static List<String> getDuplicateToolNames(ToolCallback... toolCallbacks) {
        Assert.notNull(toolCallbacks, "toolCallbacks cannot be null");
        return getDuplicateToolNames(Arrays.asList(toolCallbacks));
    }
}
