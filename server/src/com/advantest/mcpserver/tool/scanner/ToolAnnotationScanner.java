package com.advantest.mcpserver.tool.scanner;

import com.advantest.mcpserver.tool.annotation.Tool;
import com.advantest.mcpserver.tool.method.MethodToolCallback;
import com.advantest.mcpserver.tool.support.ToolDefinitions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public class ToolAnnotationScanner {
    public static List<MethodToolCallback> findToolAnnotatedMethods(String packageName) {
        List<MethodToolCallback> tools = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Package path does not exist: " + path);
        }
        try {
            File directory = new File(resource.toURI());
            if (!directory.exists()) {
                return tools;
            }
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.getName().endsWith("Service.class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    Object object = clazz.getDeclaredConstructor().newInstance();
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Tool.class)) {
                            MethodToolCallback toolCallback = MethodToolCallback.builder()
                                    .toolObject(object)
                                    .toolMethod(method)
                                    .toolDefinition(ToolDefinitions.from(method))
                                    .build();
                            tools.add(toolCallback);
                        }
                    }
                }
            }
            return tools;
        } catch (URISyntaxException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
