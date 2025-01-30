package fun.fengwk.chatjava.example.springboot;

import fun.fengwk.chatjava.core.client.ChatClient;
import fun.fengwk.chatjava.core.client.ChatClientProvider;
import fun.fengwk.chatjava.core.client.ChatCompletionsResponse;
import fun.fengwk.chatjava.core.client.StreamChatListener;
import fun.fengwk.chatjava.core.client.request.ChatMessage;
import fun.fengwk.chatjava.core.client.request.ChatRequest;
import fun.fengwk.chatjava.core.client.util.ChatUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author fengwk
 */
@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 启动SpringBoot应用
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ExampleApplication.class, args);

        // 获取ChatClientProvider
        ChatClientProvider chatClientProvider = applicationContext.getBean(ChatClientProvider.class);
        // 获取ChatClient
        ChatClient chatClient = chatClientProvider.getChatClient();
        //构建请求参数
        ChatRequest chatRequest = new ChatRequest();
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.newUserMessage("问候语“吃了么”在英语里怎么说"));
        chatRequest.setMessages(messages);
        chatRequest.setModel("deepseek-chat");
        // 流式调用
        CompletableFuture<ChatCompletionsResponse> future = chatClient.streamChatCompletions(chatRequest, new StreamChatListener() {
            @Override
            public void onReceive(ChatCompletionsResponse response) {
                System.out.print(ChatUtils.getContent(response.getChatResponse()));
            }
            @Override
            public void onComplete() {
                System.out.println();
            }
        });

        future.get();
    }

}
