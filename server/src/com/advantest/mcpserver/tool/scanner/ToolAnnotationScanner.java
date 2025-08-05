package com.advantest.mcpserver.tool.scanner;

import java.io.File;
import java.lang.annotation.Annotation;
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
    public static List<Method> findToolAnnotatedMethods(String packageName, Class<? extends Annotation> annotationClass) {
        List<Method> annotatedMethods = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Package path does not exist: " + path);
        }
        try {
            File directory = new File(resource.toURI());
            if (!directory.exists()) {
                return annotatedMethods;
            }
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(annotationClass)) {
                            annotatedMethods.add(method);
                        }
                    }
                }
            }
            return annotatedMethods;
        } catch (URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
