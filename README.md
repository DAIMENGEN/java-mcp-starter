# Model Context Protocol Java Starter

This project demonstrates a Java implementation of the Model Context Protocol (MCP) client that can communicate with an MCP server and leverage tools provided by the server.

## Project Structure

The project consists of two main modules:

1. **client** - The main MCP client implementation
2. **server** - A basic server template (currently minimal)

## Client Implementation

The client implementation includes the following key components:

### Core Classes

- [Main.java](file://D:\AI\mcp\java-mcp-starter\client\src\Main.java) - Entry point demonstrating the complete flow of:
    - Connecting to an MCP server
    - Listing available tools
    - Calling a language model with tool integration
    - Handling tool execution requests from the model
    - Processing and displaying responses

### Model Classes

- `AssistantMessage` - Represents assistant messages with potential tool calls
- `ChatMessage` - Basic chat message with role and content
- [ChatModel](file://D:\AI\mcp\java-mcp-starter\client\src\com\advantest\mcpclient\ChatModel.java#L10-L15) - Interface for chat models
- [ChatRequest](file://D:\AI\mcp\java-mcp-starter\client\src\com\advantest\mcpclient\ChatRequest.java#L13-L82) - Request object for chat API calls
- `ChatResponse` - Response object from chat API with streaming support
- `FunctionCall` - Represents a function call request
- [MessageType](file://D:\AI\mcp\java-mcp-starter\client\src\com\advantest\mcpclient\MessageType.java#L7-L31) - Enum for different message roles (user, assistant, system, tool)
- [OllamaChatModel](file://D:\AI\mcp\java-mcp-starter\client\src\com\advantest\mcpclient\OllamaChatModel.java#L12-L55) - Implementation for Ollama chat models with streaming support
- `Parameters` - Tool parameter definitions
- [Tool](file://D:\AI\mcp\java-mcp-starter\client\src\com\advantest\mcpclient\Tool.java#L7-L21) - Tool definition with function information
- `ToolCall` - Represents a tool call request
- `ToolFunction` - Function definition for tools

### Key Features

1. **MCP Integration**: Connects to an MCP server and discovers available tools
2. **Tool Usage**: Automatically converts MCP tools to chat model tools
3. **Streaming Support**: Processes streaming responses from the language model
4. **Tool Execution**: Handles tool execution requests from the model and provides results back
5. **Reactive Programming**: Uses Reactor framework for asynchronous operations

## How It Works

1. The client connects to an MCP server at `http://localhost:8080`
2. It retrieves the list of available tools from the server
3. It converts these tools into a format compatible with the Ollama chat model
4. It sends a user query ("请告诉所有的班车信息") to the Ollama model at `http://10.150.10.125:11434`
5. If the model requests tool execution:
    - The client executes the requested tool via the MCP server
    - It sends the tool results back to the model for further processing
6. The final response is printed to the console

## Prerequisites

- Java 17 or higher
- An MCP server running at `http://localhost:8080`
- Ollama server running at `http://10.150.10.125:11434` with a model (default: qwen3:8b)

## Setup and Running

1. Ensure the MCP server is running at `http://localhost:8080`
2. Ensure Ollama is running at `http://10.150.10.125:11434`
3. Compile the project:
   ```bash
   javac -cp "lib/*" src/com/advantest/mcpclient/*.java src/Main.java
   ```


4. Run the client:
   ```bash
   java -cp "src:lib/*" Main
   ```


## Customization

You can modify the following in [Main.java](file://D:\AI\mcp\java-mcp-starter\client\src\Main.java):
- Ollama server URL
- Model name in [OllamaChatModel](file://D:\AI\mcp\java-mcp-starter\client\src\com\advantest\mcpclient\OllamaChatModel.java#L12-L55)
- User prompt
- Timeout duration

## Dependencies

The project requires the following libraries:
- Jackson for JSON processing
- Reactor for reactive programming
- Model Context Protocol SDK

These should be included in the `lib` directory or configured in your build system.

## Notes

- The current implementation uses a fixed user prompt ("请告诉所有的班车信息")
- Error handling is basic and can be enhanced for production use
- The timeout is set to 5 minutes which should be sufficient for most operations

## Key Implementation Details

Looking at the [Main.java](file://D:\AI\mcp\java-mcp-starter\client\src\Main.java) implementation, the application follows this flow:

1. Initializes connection to the MCP server using [HttpClientSseClientTransport](file://io\modelcontextprotocol\client\transport\HttpClientSseClientTransport.java#L16-L63)
2. Retrieves available tools from the MCP server using `client.listTools()`
3. Maps MCP tools to internal [Tool](file://io\modelcontextprotocol\spec\McpSchema.java#L573-L587) objects with their functions and parameters
4. Sends a query to the Ollama model with the tools using streaming
5. Processes the model's response:
    - If tool execution is required, calls the tool via the MCP server
    - Sends tool results back to the model for further processing
    - Displays the final response to the console