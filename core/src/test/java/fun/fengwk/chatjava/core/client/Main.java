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
        chatRequest.setMessages(Collections.singletonList(ChatMessage.newUserMessage("你好呀")));

        // 测试普通模式
        System.out.println("chatCompletions-------------------------");
        ChatCompletionsResponse response = chatClient.chatCompletions(chatRequest);

        System.out.println("chatCompletions: " + ChatJsonUtils.toJson(response.getChatResponse()));

        // 流式模式
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
