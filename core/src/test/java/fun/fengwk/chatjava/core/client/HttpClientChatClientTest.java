package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.request.ChatMessage;
import fun.fengwk.chatjava.core.client.request.ChatRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.ExecutionException;


/**
 * @author fengwk
 */
public class HttpClientChatClientTest {

    @Test
    public void testHttpChatCompletions() throws ExecutionException, InterruptedException {
        // 添加本地代理
//        List<ProxyBuilder> proxies = new ArrayList<>();
//        ProxyBuilder httpProxyConfig = new ProxyBuilder();
//        httpProxyConfig.setHostname("127.0.0.1");
//        httpProxyConfig.setPort(7890);
//        httpProxyConfig.setType(Proxy.Type.HTTP);
//        proxies.add(httpProxyConfig);
//        ProxyBuilder socksProxyConfig = new ProxyBuilder();
//        socksProxyConfig.setHostname("127.0.0.1");
//        socksProxyConfig.setPort(7891);
//        socksProxyConfig.setType(Proxy.Type.SOCKS);
//        proxies.add(socksProxyConfig);

        DefaultChatClientProvider chatClientProvider = new DefaultChatClientProvider();
        ChatClient chatClient = chatClientProvider.getChatClient();
        Assertions.assertNotNull(chatClient);

        ChatRequest chatRequest = new ChatRequest();
//        chatRequest.setModel("deepseek-chat");
        chatRequest.setModel("deepseek-reasoner");
        chatRequest.setMessages(Collections.singletonList(ChatMessage.newUserMessage("你好呀～")));

        // 测试普通模式
        ChatClientTestUtils.testChatCompletions(chatClient, chatRequest);

        // 测试流式模式
        ChatClientTestUtils.testStreamChatCompletions(chatClient, chatRequest);
    }

}
