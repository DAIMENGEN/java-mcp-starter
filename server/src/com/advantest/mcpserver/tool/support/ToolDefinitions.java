package com.advantest.mcpserver.tool.support;

import com.advantest.mcpserver.tool.schema.InputSchema;
import com.advantest.mcpserver.tool.schema.Property;
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

    private static void addParameterToSchema(InputSchema.Builder schemaBuilder, Parameter parameter) {
        String name = parameter.getName();
        String type = parameter.getType().getName();
        ToolParam annotation = parameter.getAnnotation(ToolParam.class);
        Property property;
        if (annotation != null) {
            String description = annotation.description();
            property = StringUtils.hasText(description)
                    ? Property.of(type, description)
                    : Property.of(type);
            if (annotation.required()) {
                schemaBuilder.required(name);
            }
        } else {
            property = Property.of(type);
        }
        schemaBuilder.addProperty(name, property);
    }

    public static DefaultToolDefinition.Builder builder(Method method) {
        Assert.notNull(method, "method cannot be null");
        InputSchema.Builder inputSchemaBuilder = InputSchema.builder();
        for (Parameter parameter : method.getParameters()) {
            addParameterToSchema(inputSchemaBuilder, parameter);
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
