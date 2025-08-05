package com.advantest.mcpserver.tool.method;

import com.advantest.mcpserver.tool.ToolCallback;
import com.advantest.mcpserver.tool.definition.ToolDefinition;
import com.advantest.mcpserver.tool.execution.ToolExecutionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public final class MethodToolCallback implements ToolCallback {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final Object toolObject;

    private final Method toolMethod;

    private final ToolDefinition toolDefinition;

    private MethodToolCallback(Builder builder) {
        this.toolObject = builder.toolObject;
        this.toolMethod = builder.toolMethod;
        this.toolDefinition = builder.toolDefinition;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return this.toolDefinition;
    }

    @Override
    public String call(Map<String, Object> toolInput) {
        Parameter[] parameters = this.toolMethod.getParameters();
        List<Object> methodArguments = Stream.of(parameters).map(parameter -> {
            Class<?> clazz = parameter.getType();
            Object parameterValue = toolInput.get(parameter.getName());
            return (Object) objectMapper.convertValue(parameterValue, clazz);
        }).toList();
        try {
            Object object = this.toolMethod.invoke(this.toolObject, methodArguments.toArray());
            return objectMapper.writeValueAsString(object);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ToolExecutionException(this.toolDefinition, ex.getCause());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Object toolObject;
        private Method toolMethod;
        private ToolDefinition toolDefinition;

        public Builder toolObject(Object toolObject) {
            this.toolObject = toolObject;
            return this;
        }

        public Builder toolMethod(Method toolMethod) {
            this.toolMethod = toolMethod;
            return this;
        }

        public Builder toolDefinition(ToolDefinition toolDefinition) {
            this.toolDefinition = toolDefinition;
            return this;
        }

        public MethodToolCallback build() {
            if (toolMethod == null || toolDefinition == null) {
                throw new IllegalStateException("toolMethod and toolDefinition must not be null");
            }
            return new MethodToolCallback(this);
        }
    }
}
