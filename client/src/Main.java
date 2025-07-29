import com.advantest.mcpclient.OllamaChatModel;
import com.advantest.mcpclient.Parameters;
import com.advantest.mcpclient.Tool;
import com.advantest.mcpclient.ToolFunction;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Created on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        CountDownLatch latch = new CountDownLatch(1);
        OllamaChatModel chatModel = new OllamaChatModel("http://10.150.10.125:11434");
        HttpClientSseClientTransport transport = HttpClientSseClientTransport.builder("http://localhost:8080").build();
        McpAsyncClient client = McpClient.async(transport).build();
        client.initialize()
                .flatMap(initResult -> client.listTools())
                .flatMap(toolList -> {
                    List<Tool> tools = toolList.tools().stream().map(tool -> Tool.function(ToolFunction.of(
                            tool.name(),
                            tool.description(),
                            Parameters.object(tool.inputSchema().properties())
                    ))).toList();
                    return Mono.just(tools);
                })
                .doOnNext(tools -> {
                    System.out.println("========================================");
                    System.out.println("🧠 Welcome to AI Chat CLI");
                    System.out.println("Type your questions below and press [Enter].");
                    System.out.println("Type \"exit\" or \"quit\" to end the session.");
                    System.out.println("========================================\n");
                    loopChat(scanner, chatModel, client, tools, latch);
                })
                .subscribe();
        latch.await();
    }

    private static void loopChat(Scanner scanner, OllamaChatModel chatModel, McpAsyncClient client, List<Tool> tools, CountDownLatch latch) {
        System.out.print("> ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
            System.out.println("\n👋 Session ended. Goodbye!");
            latch.countDown();
            return;
        }
        if (input.isEmpty()) {
            System.out.println("⚠️  Empty input. Please enter a valid query.");
            loopChat(scanner, chatModel, client, tools, latch);
            return;
        }
        System.out.println("🤖 AI is thinking...\n");
        chatModel.tools(tools).stream(input)
                .subscribeOn(Schedulers.parallel())
                .concatMap(chatResponse -> {
                    if (chatResponse.requiresToolExecution()) {
                        return Flux.fromIterable(chatResponse.message().toolCalls())
                                .concatMap(toolCall -> {
                                    McpSchema.CallToolRequest callToolRequest = new McpSchema.CallToolRequest(
                                            toolCall.function().name(),
                                            toolCall.function().arguments()
                                    );
                                    return client.callTool(callToolRequest)
                                            .flatMapMany(callToolResult -> {
                                                McpSchema.Content content = callToolResult.content().getFirst();
                                                if ("text".equals(content.type())) {
                                                    McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                                                    String toolResult = textContent.text();
                                                    return chatModel.tools(null).stream(input + " " + toolResult)
                                                            .subscribeOn(Schedulers.parallel())
                                                            .doOnNext(r -> {
                                                                if (!r.done()) {
                                                                    String c = r.message().content();
                                                                    System.out.print(c.equals("\n") ? "\n" : c);
                                                                }
                                                            });
                                                }
                                                return Flux.empty();
                                            });
                                })
                                .then(); // 等所有工具调用处理完
                    } else {
                        // 普通响应
                        if (chatResponse.message() != null && chatResponse.message().content() != null) {
                            String content = chatResponse.message().content();
                            System.out.print(content.equals("\n") ? "\n" : content);
                        }
                        return Mono.empty();
                    }
                })
                .doOnError(err -> System.err.println("❌ 错误发生：" + err.getMessage()))
                .doOnTerminate(() -> {
                    System.out.println("\n");
                    loopChat(scanner, chatModel, client, tools, latch); // 继续下一轮对话
                })
                .subscribe();
    }
}