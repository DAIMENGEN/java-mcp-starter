package com.advantest.mcpserver;

import com.advantest.mcpserver.tool.annotation.Tool;
import com.advantest.mcpserver.tool.annotation.ToolParam;
import com.advantest.mcpserver.tool.annotation.ToolService;

/**
 * Create on 2025/08/05
 * Author: mengen.dai@advantest.com
 */
@ToolService
public class WeatherService {
    @Tool(name = "getWeather", description = "获取指定城市的天气情况")
    public String getWeather(@ToolParam(description = "要查询天气的城市名称") String city) {
        return String.format("工具返回结果：当前 %s 的天气是：晴，气温 28°C，湿度 60%%，微风，估计有小雨。", city);
    }
}
