package com.advantest.mcpserver.tool.scanner;

import com.advantest.mcpserver.tool.annotation.ToolService;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public class ToolAnnotationScanner {

    private ToolAnnotationScanner() {
    }

    public static List<Object> findToolAnnotatedMethods(String packageName) {
        List<Object> toolObjects = new ArrayList<>();
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(ToolService.class);
        for (Class<?> clazz : annotatedClasses) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                toolObjects.add(instance);
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to instantiate class: " + clazz.getName(), e);
            }
        }
        return toolObjects;
    }
}
