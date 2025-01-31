[English](./README.md) | [简体中文](./docs/README.zh-CN.md)

## Deepseek Java (Client)

Provides Deepseek Java Client and some high-level Agent encapsulations.
Since Deepseek is compatible with the ChatGPT protocol, theoretically, this can also serve as a ChatGPT client.

## Features

- Deepseek Java Client
- Provides Agent Memory abstraction and an Agent Engine for executing multi-turn dialogues
- Integration with Spring Boot
- All dependencies are optional and should be integrable into most projects
- Includes a simple command-line Q&A tool example
 
## Requirements

- JDK11 or higher is required due to the use of JDK HttpClient library
- For backward compatibility, you can reimplement the ChatClient interface using OkHttp
- To run the code in this project, it is recommended to configure the environment variable `CHAT_API_KEY`, as the API token is read from here by default

```shell
export CHAT_API_KEY=${YourToken}
```

## Usage

### Client

To use the client directly, you need to depend on `chat-java-core` and any JSON library:

- jackson
- gson
- fastjson
 
```xml
<dependencies>
    <dependency>
        <groupId>fun.fengwk.chat-java</groupId>
        <artifactId>chat-java-core</artifactId>
        <version>0.0.1</version>
    </dependency>
    <!-- You can choose one of the following JSON libraries -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>${yourVersion}</version>
    </dependency>
<!--    <dependency>-->
<!--        <groupId>com.google.code.gson</groupId>-->
<!--        <artifactId>gson</artifactId>-->
<!--        <version>${yourVersion}</version>-->
<!--    </dependency>-->
<!--    <dependency>-->
<!--        <groupId>com.alibaba</groupId>-->
<!--        <artifactId>fastjson</artifactId>-->
<!--        <version>${yourVersion}</version>-->
<!--    </dependency>-->
</dependencies>
```

The logging framework uses slf4j, so you need to have a corresponding implementation. The example uses slf4j-simple, but generally, you can use the slf4j implementation or bridge of your application's default logging framework.

If you need to use ChatGPT token counting, you need to depend on `jtokkit`

```xml
<dependency>
    <groupId>com.knuddels</groupId>
    <artifactId>jtokkit</artifactId>
    <version>${yourVersion}</version>
</dependency>
```

You can now start using ChatClient

```java
package fun.fengwk.chatjava.core.client;
import fun.fengwk.chatjava.core.client.request.ChatMessage;
import fun.fengwk.chatjava.core.client.request.ChatRequest;
import fun.fengwk.chatjava.core.client.util.json.ChatJsonUtils;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
/**
 * @author fengwk
 */
public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        DefaultChatClientProvider chatClientProvider = new DefaultChatClientProvider();
        ChatClient chatClient = chatClientProvider.getChatClient();
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("deepseek-reasoner");
        chatRequest.setMessages(Collections.singletonList(ChatMessage.newUserMessage("Hello")));
        // Normal mode
        System.out.println("chatCompletions-------------------------");
        ChatCompletionsResponse response = chatClient.chatCompletions(chatRequest);
        System.out.println("chatCompletions: " + ChatJsonUtils.toJson(response.getChatResponse()));
        // Stream mode
        System.out.println("streamChatCompletions-------------------------");
        CompletableFuture<ChatCompletionsResponse> future = chatClient.streamChatCompletions(
                chatRequest, new StreamChatListener() {
                    @Override
                    public void onReceive(ChatCompletionsResponse response) {
                        System.out.println("onReceive: " + ChatJsonUtils.toJson(response.getChatResponse()));
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        System.err.println("onError: " + throwable);
                    }
                    @Override
                    public void onComplete() {
                        System.out.println("onComplete----------------------------");
                    }
                });
        response = future.get();
        System.out.println("complete: " + ChatJsonUtils.toJson(response.getChatResponse()));
    }
}
```

The client configuration `ChatClientOptions` defines a series of default values, which you can reset

```java
@Data
public class ChatClientOptions {
    /**
     * Completion URL
     */
    private URI chatCompletionsUrl = ChatUrls.DEEPSEEK_CHAT_COMPLETIONS;
    /**
     * Deepseek token, defaults to using the environment variable CHAT_API_KEY
     */
    private String token = System.getenv("CHAT_API_KEY");
    /**
     * Timeout for each HTTP request
     */
    private Duration perHttpRequestTimeout = ChatHttpClientFactory.getDefaultTimeout();
    /**
     * To avoid the existing function calling bug in deepseek causing continuous loop calls and consuming tokens, a maximum number of loop calls needs to be set
     */
    private int maxFunctionCallTimes = 3;
}
```

### Agent

Agent is a wrapper for ChatClient. Through AgentEngine, you can easily implement multi-turn dialogues. I have provided a simple command-line dialogue implementation in `example/cmd`:

[Main.java](./example/cmd/src/main/java/fun/fengwk/chatjava/example/cmd/Main.java)

![cmdpic](./docs/cmdpic.png)

### SpringBoot

A Spring Boot Starter is provided for quick integration into Spring Boot projects. You only need to depend on the following configuration:

```xml
<dependencies>
    <dependency>
        <groupId>fun.fengwk.chat-java</groupId>
        <artifactId>chat-java-starter</artifactId>
        <version>0.0.1</version>
    </dependency>
    <!-- A complete SpringBoot project usually already includes one of these three JSON libraries -->
<!--    <dependency>-->
<!--        <groupId>com.fasterxml.jackson.core</groupId>-->
<!--        <artifactId>jackson-databind</artifactId>-->
<!--        <version>${yourVersion}</version>-->
<!--    </dependency>-->
<!--    <dependency>-->
<!--        <groupId>com.google.code.gson</groupId>-->
<!--        <artifactId>gson</artifactId>-->
<!--        <version>${yourVersion}</version>-->
<!--    </dependency>-->
<!--    <dependency>-->
<!--        <groupId>com.alibaba</groupId>-->
<!--        <artifactId>fastjson</artifactId>-->
<!--        <version>${yourVersion}</version>-->
<!--    </dependency>-->
</dependencies>
```

If your SpringBoot project references other JSON libraries and you do not want to include the preset three JSON libraries, you can implement ChatJsonAdapterProvider and add it to the ServiceLoader's SPI to introduce your project's own JSON library implementation.

The SpringBoot configuration class is `ChatClientProperties`, and you can configure them through the `chat.client` prefix key:

```java
@Data
@ConfigurationProperties("chat.client")
public class ChatClientProperties {
    /**
     * Proxy configuration list, in host:port format
     */
    private List<ProxyBuilder> proxies = Collections.emptyList();
    /**
     * Connection timeout
     */
    private Duration connectTimeout = Duration.ofSeconds(5);
    /**
     * Default client configuration
     */
    private ChatClientOptions defaultChatClientOptions = new ChatClientOptions();
}
```
### Function Calling

Using the `@ToolFunction` annotation, a public method can be provided as a tool function for the large model to call. Through `ToolFunctionHandlerRegistry#registerBeanIfNecessary`, all tool methods in an object can be registered to `ChatClientProvider`. For reference, see:

- [ToolChatClientTest.java](./core/src/test/java/fun/fengwk/chatjava/core/client/tool/ToolChatClientTest.java)
- [ToolFunctionsDemo.java](./core/src/test/java/fun/fengwk/chatjava/core/client/tool/ToolFunctionsDemo.java)

If integrated with SpringBoot, you can use the `@ToolFunction` annotation on any bean to annotate tool methods. These tool methods will automatically be added to the auto-injected `ChatClientProvider`.
