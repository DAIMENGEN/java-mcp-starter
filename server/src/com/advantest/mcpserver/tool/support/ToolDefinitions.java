package com.advantest.mcpserver.tool.support;

import com.advantest.mcpserver.tool.definition.DefaultToolDefinition;
import com.advantest.mcpserver.tool.definition.ToolDefinition;
import io.modelcontextprotocol.util.Assert;

import java.lang.reflect.Method;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public final class ToolDefinitions {
    private ToolDefinitions() {
    }

    public static DefaultToolDefinition.Builder builder(Method method) {
        Assert.notNull(method, "method cannot be null");
        return DefaultToolDefinition.builder()
                .name(ToolUtils.getToolName(method))
                .description(ToolUtils.getToolDescription(method))
                .inputSchema("JsonSchemaGenerator");
    }

    public static ToolDefinition from(Method method) {
        return builder(method).build();
    }
}
