package com.advantest.mcpserver.tool.support;

import com.advantest.mcpserver.tool.annotation.Tool;
import io.modelcontextprotocol.util.Assert;

import java.lang.reflect.Method;

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
}
