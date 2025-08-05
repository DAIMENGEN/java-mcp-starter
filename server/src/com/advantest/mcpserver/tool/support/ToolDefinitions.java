package com.advantest.mcpserver.tool.support;

import com.advantest.mcpserver.InputSchema;
import com.advantest.mcpserver.Property;
import com.advantest.mcpserver.tool.annotation.ToolParam;
import com.advantest.mcpserver.tool.definition.DefaultToolDefinition;
import com.advantest.mcpserver.tool.definition.ToolDefinition;
import com.advantest.mcpserver.utils.StringUtils;
import io.modelcontextprotocol.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public final class ToolDefinitions {
    private ToolDefinitions() {
    }

    public static DefaultToolDefinition.Builder builder(Method method) {
        Assert.notNull(method, "method cannot be null");
        InputSchema.Builder inputSchemaBuilder = InputSchema.builder();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            String parameterName = parameter.getName();
            String parameterType = parameter.getType().getName();
            ToolParam annotation = parameter.getAnnotation(ToolParam.class);
            if (annotation != null) {
                String description = annotation.description();
                boolean hasText = StringUtils.hasText(description);
                inputSchemaBuilder.addProperty(parameterName, hasText ? Property.of(parameterType, description) : Property.of(parameterType));
                if (annotation.required()) {
                    inputSchemaBuilder.required(parameterName);
                }
            } else {
                inputSchemaBuilder.addProperty(parameterName, Property.of(parameterType));
            }
        }
        String inputSchema = inputSchemaBuilder.build().toJson();
        return DefaultToolDefinition.builder()
                .name(ToolUtils.getToolName(method))
                .description(ToolUtils.getToolDescription(method))
                .inputSchema(inputSchema);
    }

    public static ToolDefinition from(Method method) {
        return builder(method).build();
    }
}
