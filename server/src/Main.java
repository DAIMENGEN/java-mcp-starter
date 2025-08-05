import com.advantest.mcpserver.InputSchema;
import com.advantest.mcpserver.Property;
import com.advantest.mcpserver.tool.annotation.Tool;
import com.advantest.mcpserver.tool.scanner.ToolAnnotationScanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public class Main {
    public static void main(String[] args) throws Exception {
        test();
        HttpServletStreamableServerTransportProvider transportProvider = HttpServletStreamableServerTransportProvider.builder()
                .objectMapper(new ObjectMapper())
                .mcpEndpoint("/mcp")
                .build();
        McpSyncServer mcpSyncServer = McpServer.sync(transportProvider)
                .serverInfo("sync-server", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .resources(false, true)
                        .tools(true)
                        .prompts(true)
                        .logging()
                        .completions()
                        .build()
                ).build();
        var syncToolSpecification = getSyncToolSpecification();
        mcpSyncServer.addTool(syncToolSpecification);
        Server server = new Server(8080);
        Connector connector = new ServerConnector(server, 1, 1, new HttpConnectionFactory());
        server.addConnector(connector);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");
        contextHandler.addServlet(new ServletHolder(transportProvider), "/mcp");
        server.setHandler(contextHandler);
        server.start();
    }

    private static SyncToolSpecification getSyncToolSpecification() {
        InputSchema inputSchema = InputSchema.builder()
                .addProperty("username", Property.of("string", "The name of the person being greeted"))
                .required("username")
                .build();
        McpSchema.Tool build = McpSchema.Tool.builder()
                .name("greeting")
                .title("greeting")
                .description("Basic greeting")
                .inputSchema(inputSchema.toJson())
                .build();
        return SyncToolSpecification.builder()
                .tool(build)
                .callHandler((exchange, request) -> {
                    System.out.println("request = " + request);
                    Object username = request.arguments().get("username");
                    System.out.println("username = " + username);
                    return new McpSchema.CallToolResult("Hello, " + username + ", I am Mengen.dai. How can I assist you today?", false);
                })
                .build();
    }


    public static void test() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Method> methods = ToolAnnotationScanner.findToolAnnotatedMethods("com.advantest.mcpserver", Tool.class);
        for (Method method : methods) {
            Tool annotation = method.getAnnotation(Tool.class);
            System.out.println("方法名：" + annotation.name());
            System.out.println("方法描述：" + annotation.description());
            System.out.println("方法参数：" + Arrays.toString(method.getParameters()));
            System.out.println("方法返回值类型：" + method.getReturnType());
            Class<?> clazz = method.getDeclaringClass();
            Object newInstance = clazz.getDeclaredConstructor().newInstance();
            Object invoke = method.invoke(newInstance, "代蒙恩");
            System.out.println(invoke);
        }
    }

}