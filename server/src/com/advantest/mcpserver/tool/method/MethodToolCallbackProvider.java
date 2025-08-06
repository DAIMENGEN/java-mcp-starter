package com.advantest.mcpserver.tool.method;

import com.advantest.mcpserver.tool.ToolCallback;
import com.advantest.mcpserver.tool.ToolCallbackProvider;
import com.advantest.mcpserver.tool.annotation.Tool;
import com.advantest.mcpserver.tool.support.ToolDefinitions;
import com.advantest.mcpserver.tool.support.ToolUtils;
import io.modelcontextprotocol.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create on 2025/08/06
 * Author: mengen.dai@advantest.com
 */
public class MethodToolCallbackProvider implements ToolCallbackProvider {

    private final List<Object> toolObjects;

    public MethodToolCallbackProvider(List<Object> toolObjects) {
        Assert.notNull(toolObjects, "toolObjects cannot be null");
        this.assertToolAnnotatedMethodsPresent(toolObjects);
        this.toolObjects = toolObjects;
        this.validateToolCallbacks(this.getToolCallbacks());
    }

    private void assertToolAnnotatedMethodsPresent(List<Object> toolObjects) {
        for (Object toolObject : toolObjects) {
            List<Method> methods = Stream.of(toolObject.getClass().getDeclaredMethods()).filter(this::isToolAnnotatedMethod).toList();
            if (methods.isEmpty()) {
                throw new IllegalStateException("No tool annotated methods found in tool object: " + toolObject.getClass().getName());
            }
        }
    }

    private boolean isToolAnnotatedMethod(Method method) {
        Tool annotation = method.getAnnotation(Tool.class);
        return Objects.nonNull(annotation);
    }

    private void validateToolCallbacks(ToolCallback[] toolCallbacks) {
        List<String> duplicateToolNames = ToolUtils.getDuplicateToolNames(toolCallbacks);
        if (!duplicateToolNames.isEmpty()) {
            throw new IllegalStateException("Multiple tools with the same name (%s) found in sources: %s".formatted(String.join(", ", duplicateToolNames), this.toolObjects.stream().map((o) -> o.getClass().getName()).collect(Collectors.joining(", "))));
        }
    }

    @Override
    public ToolCallback[] getToolCallbacks() {
        ToolCallback[] toolCallbacks = this.toolObjects.stream().flatMap(object -> {
            Method[] methods = object.getClass().getDeclaredMethods();
            return Arrays.stream(methods).filter(this::isToolAnnotatedMethod).map(method -> MethodToolCallback.builder()
                    .toolObject(object)
                    .toolMethod(method)
                    .toolDefinition(ToolDefinitions.from(method))
                    .build());
        }).toArray(ToolCallback[]::new);
        this.validateToolCallbacks(toolCallbacks);
        return toolCallbacks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private List<Object> toolObjects;

        private Builder() {
        }

        public Builder toolObjects(Object... toolObjects) {
            Assert.notNull(toolObjects, "toolObjects cannot be null");
            this.toolObjects = Arrays.asList(toolObjects);
            return this;
        }

        public MethodToolCallbackProvider build() {
            return new MethodToolCallbackProvider(this.toolObjects);
        }
    }
}
