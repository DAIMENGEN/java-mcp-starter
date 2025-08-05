package com.advantest.mcpserver;

import com.advantest.mcpserver.tool.annotation.Tool;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public class Service {

    @Tool(name = "这是方法的名字", description = "这是一个测试")
    public String test(String name) {
        return name + "_123";
    }

}
