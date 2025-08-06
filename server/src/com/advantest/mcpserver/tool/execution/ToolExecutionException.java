package com.advantest.mcpserver.tool.execution;

import com.advantest.mcpserver.tool.definition.ToolDefinition;

import java.io.Serial;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public class ToolExecutionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 20250805221099L;
    private final ToolDefinition toolDefinition;

    public ToolExecutionException(ToolDefinition toolDefinition, Throwable cause) {
        super(cause.getMessage(), cause);
        this.toolDefinition = toolDefinition;
    }

    public ToolDefinition getToolDefinition() {
        return this.toolDefinition;
    }
}
