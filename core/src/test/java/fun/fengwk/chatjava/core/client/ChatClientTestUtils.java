package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.request.ChatRequest;
import fun.fengwk.chatjava.core.client.util.ChatUtils;
import fun.fengwk.chatjava.core.client.util.json.ChatJsonUtils;
import org.junit.platform.commons.util.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fengwk
 */
public class ChatClientTestUtils {

    private ChatClientTestUtils() {}

    public static ChatCompletionsResponse testChatCompletions(ChatClient chatClient, ChatRequest chatRequest) {
        System.out.println("chatCompletions-------------------------");
        ChatCompletionsResponse response = chatClient.chatCompletions(chatRequest);

        System.out.println("chatCompletions: " + ChatJsonUtils.toJson(response.getChatResponse()));

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertTrue(StringUtils.isNotBlank(ChatUtils.getContent(response.getChatResponse())));
        return response;
    }

    public static ChatCompletionsResponse testStreamChatCompletions(ChatClient chatClient, ChatRequest chatRequest)
        throws ExecutionException, InterruptedException {
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

        ChatCompletionsResponse response = future.get();
        System.out.println("complete: " + ChatJsonUtils.toJson(response.getChatResponse()));

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertTrue(StringUtils.isNotBlank(ChatUtils.getContent(response.getChatResponse())));
        return response;
    }

}
