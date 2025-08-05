import com.advantest.mcpserver.tool.method.MethodToolCallback;
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

import java.util.List;

/**
 * Create on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public class Main {
    public static void main(String[] args) throws Exception {
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
        List<SyncToolSpecification> syncToolSpecifications = getSyncToolSpecifications();
        for (SyncToolSpecification syncToolSpecification : syncToolSpecifications) {
            mcpSyncServer.addTool(syncToolSpecification);
        }
        Server server = new Server(8080);
        Connector connector = new ServerConnector(server, 1, 1, new HttpConnectionFactory());
        server.addConnector(connector);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");
        contextHandler.addServlet(new ServletHolder(transportProvider), "/mcp");
        server.setHandler(contextHandler);
        server.start();
    }

    public static List<SyncToolSpecification> getSyncToolSpecifications() {
        List<MethodToolCallback> tools = ToolAnnotationScanner.findToolAnnotatedMethods("com.advantest.mcpserver");
        return tools.stream().map(toolCallback -> {
            String name = toolCallback.getToolDefinition().name();
            String description = toolCallback.getToolDefinition().description();
            String inputSchema = toolCallback.getToolDefinition().inputSchema();
            McpSchema.Tool tool = McpSchema.Tool.builder()
                    .name(name)
                    .title(name)
                    .description(description)
                    .inputSchema(inputSchema)
                    .build();
            return SyncToolSpecification.builder()
                    .tool(tool)
                    .callHandler((exchange, request) -> new McpSchema.CallToolResult(toolCallback.call(request.arguments()), false)).build();
        }).toList();
    }
}