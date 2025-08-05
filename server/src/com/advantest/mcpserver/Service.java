package com.advantest.mcpserver;

import com.advantest.mcpserver.tool.annotation.Tool;
import com.advantest.mcpserver.tool.annotation.ToolParam;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
public class Service {

    @Tool(name = "greeting", description = "这是是一个问候方法")
    public String greeting(@ToolParam(description = "这个是被问候的人的性命") String name) {
        return "工具返回结果：你好,"+name+"！我是代蒙恩，请问今天有什么可以帮助你的吗？";
    }

    @Tool(name = "getWeather", description = "获取指定城市的天气情况")
    public String getWeather(@ToolParam(description = "要查询天气的城市名称") String city) {
        return String.format("工具返回结果：当前 %s 的天气是：晴，气温 28°C，湿度 60%%，微风，估计有小雨。", city);
    }
}
