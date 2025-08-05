package com.advantest.mcpserver.tool.annotation;

import java.lang.annotation.*;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tool {

    String name();

    String description() default "";
}
