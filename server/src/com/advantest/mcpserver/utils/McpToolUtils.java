package com.advantest.mcpserver.utils;

import com.advantest.mcpserver.tool.ToolCallback;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

/**
 * Create on 2025/08/06
 * Author: mengen.dai@advantest.com
 */
public class McpToolUtils {

    private McpToolUtils() {
    }

    public static List<McpServerFeatures.SyncToolSpecification> toSyncToolSpecification(List<ToolCallback> toolCallbacks) {
        return toolCallbacks.stream().map(McpToolUtils::toSyncToolSpecification).toList();
    }

    public static List<McpServerFeatures.SyncToolSpecification> toSyncToolSpecifications(ToolCallback... toolCallbacks) {
        return toSyncToolSpecification(List.of(toolCallbacks));
    }

    public static McpServerFeatures.SyncToolSpecification toSyncToolSpecification(ToolCallback toolCallback) {
        String name = toolCallback.getToolDefinition().name();
        String description = toolCallback.getToolDefinition().description();
        String inputSchema = toolCallback.getToolDefinition().inputSchema();
        McpSchema.Tool tool = McpSchema.Tool.builder()
                .name(name)
                .title(name)
                .description(description)
                .inputSchema(inputSchema)
                .build();
        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> new McpSchema.CallToolResult(toolCallback.call(request.arguments()), false)).build();
    }
}
