package com.advantest.mcpserver.tool.scanner;

import com.advantest.mcpserver.tool.annotation.ToolService;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
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

    private ToolAnnotationScanner() {
    }
    public static List<Object> findToolAnnotatedMethods(String packageName) {
        List<Object> toolObjects = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Package path does not exist: " + path);
        }
        try {
            File directory = new File(resource.toURI());
            if (!directory.exists()) {
                return toolObjects;
            }
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    ToolService annotation = clazz.getAnnotation(ToolService.class);
                    if (annotation == null) {
                        continue;
                    }
                    Object object = clazz.getDeclaredConstructor().newInstance();
                    toolObjects.add(object);
                }
            }
            return toolObjects;
        } catch (URISyntaxException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
