import com.advantest.mcpclient.*;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2025/07/25
 * Author: mengen.dai@advantest.com
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        OllamaChatModel chatModel = new OllamaChatModel("http://10.150.10.125:11434");
        HttpClientSseClientTransport transport = HttpClientSseClientTransport.builder("http://localhost:8080").build(); // Corrected for completeness based on imports
        McpAsyncClient client = McpClient.async(transport).build();
        // Release the latch when the entire flow completes
        client.initialize()
                .flatMap(initResult -> client.listTools())
                .flatMapMany(toolList -> { // Changed to flatMapMany because we're going from Mono to Flux of ChatResponse
                    List<Tool> tools = toolList.tools().stream().map(tool -> Tool.function(ToolFunction.of(
                            tool.name(),
                            tool.description(),
                            Parameters.object(tool.inputSchema().properties())
                    ))).toList();
                    try {
                        return chatModel.tools(tools).stream().call("请告诉所有的班车信息").subscribeOn(Schedulers.parallel()); // This Flux now emits ChatResponse
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(chatResponse -> { // This flatMap now processes each ChatResponse
                    if (chatResponse == null) {
                        return Mono.empty(); // Still Mono<Void> or whatever the chain expects next
                    }
                    if (chatResponse.done()) {
                        return Mono.empty(); // Signal completion, current chain expects a Mono<Void>
                    }
                    if (chatResponse.requiresToolExecution()) {
                        // Process tool calls asynchronously
                        return Flux.fromIterable(chatResponse.message().toolCalls())
                                .flatMap(toolCall -> {
                                    McpSchema.CallToolRequest callToolRequest = new McpSchema.CallToolRequest(toolCall.function().name(), toolCall.function().arguments());
                                    return client.callTool(callToolRequest)
                                            .flatMap(callToolResult -> {
                                                McpSchema.Content content = callToolResult.content().getFirst();
                                                if ("text".equals(content.type())) {
                                                    McpSchema.TextContent textContent = (McpSchema.TextContent) content;
                                                    String text = textContent.text();
                                                    // Call chatModel again with the result of the tool execution
                                                    try {
                                                        return chatModel.tools(null).stream().call(text)
                                                                .subscribeOn(Schedulers.parallel()) // Again, execute blocking call on parallel scheduler
                                                                .doOnNext(r -> {
                                                                    if (!r.done()) {
                                                                        String c = r.message().content();
                                                                        if (c.equals("\n")) {
                                                                            System.out.println();
                                                                        } else {
                                                                            System.out.print(c);
                                                                        }
                                                                    }
                                                                })
                                                                .then(); // Convert Flux to Mono<Void> to continue the chain
                                                    } catch (IOException | InterruptedException e) {
                                                        return Mono.error(new RuntimeException(e));
                                                    }
                                                }
                                                return Mono.empty(); // If not text content, just complete
                                            });
                                })
                                .then(); // Convert Flux<Mono<Void>> to Mono<Void> to continue the chain
                    }
                    if (chatResponse.message() != null && chatResponse.message().content() != null) {
                        String content = chatResponse.message().content();
                        if (content.equals("\n")) {
                            System.out.println();
                        } else {
                            System.out.print(content);
                        }
                    }
                    return Mono.empty(); // Continue with the next chatResponse, emitting nothing
                })
                .doOnError(error -> {
                    System.err.println("错误发生: " + error.getMessage());
                    latch.countDown(); // Release the latch on error
                })
                .doOnTerminate(latch::countDown)
                .subscribe();
        if (!latch.await(5, TimeUnit.MINUTES)) {
            System.err.println("程序超时，可能存在未完成的异步操作。");
        }
    }
}