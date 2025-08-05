package com.advantest.mcpserver.tool;

import com.advantest.mcpserver.tool.definition.ToolDefinition;

import java.util.Map;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public interface ToolCallback {
    ToolDefinition getToolDefinition();

    String call(Map<String, Object> toolInput);
}
