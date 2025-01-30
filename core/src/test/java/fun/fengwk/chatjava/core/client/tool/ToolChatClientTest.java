package fun.fengwk.chatjava.core.client.tool;

import fun.fengwk.chatjava.core.client.ChatClient;
import fun.fengwk.chatjava.core.client.ChatClientTestUtils;
import fun.fengwk.chatjava.core.client.DefaultChatClientProvider;
import fun.fengwk.chatjava.core.client.request.ChatMessage;
import fun.fengwk.chatjava.core.client.request.ChatRequest;
import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;
import fun.fengwk.chatjava.core.client.util.ChatUtils;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author fengwk
 */
public class ToolChatClientTest {

    // TODO 目前由于deepseek的问题function calling非常不稳定
//    @Test
    public void test() throws ExecutionException, InterruptedException {
        // 这个demo中包含了tool function
        ToolFunctionsDemo toolFunctionsDemo = new ToolFunctionsDemo();

        DefaultChatClientProvider chatClientProvider = new DefaultChatClientProvider();

        // 注册tool function
        ToolFunctionHandlerRegistry toolFunctionHandlerRegistry = chatClientProvider.getToolFunctionHandlerRegistry();
        toolFunctionHandlerRegistry.registerBeanIfNecessary(toolFunctionsDemo);
        Assertions.assertTrue(ChatMiscUtils.isNotEmpty(toolFunctionHandlerRegistry.getTools()));

        // 获取默认的客户端
        ChatClient chatClient = chatClientProvider.getChatClientWithTools();
        // 查询今天天气
        ChatRequest chatRequest = new ChatRequest();
        // deepseek-reasoner目前不支持function calling
        chatRequest.setModel("deepseek-chat");
        // 添加消息
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.newUserMessage("获取2025年1月3日的天气"));
        chatRequest.setMessages(messages);

        // 测试普通模式
        ChatClientTestUtils.testChatCompletions(chatClient, chatRequest);

        // 测试流式模式
        ChatUtils.streamIncludeUsage(chatRequest);
        ChatClientTestUtils.testStreamChatCompletions(chatClient, chatRequest);
    }

}
