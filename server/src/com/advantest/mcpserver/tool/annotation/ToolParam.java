package com.advantest.mcpserver.tool.annotation;

import java.lang.annotation.*;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToolParam {
    boolean required() default true;

    String description() default "";
}
