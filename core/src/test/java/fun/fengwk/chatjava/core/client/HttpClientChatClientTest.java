package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.request.ChatMessage;
import fun.fengwk.chatjava.core.client.request.ChatRequest;
import fun.fengwk.chatjava.core.client.tool.DefaultToolFunctionHandlerRegistry;
import fun.fengwk.chatjava.core.client.util.httpclient.ChatHttpClientFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
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
        ChatClientOptions defualtChatClientOptions = new ChatClientOptions();
//        defualtChatClientOptions.setChatCompletionsUrl(URI.create("http://127.0.0.1:11434/v1/chat/completions"));
        DefaultChatClientProvider chatClientProvider = new DefaultChatClientProvider(
                new DefaultToolFunctionHandlerRegistry(),
                defualtChatClientOptions,
                ChatHttpClientFactory.getDefaultHttpClient()
        );
        ChatClient chatClient = chatClientProvider.getChatClient();
        Assertions.assertNotNull(chatClient);

        ChatRequest chatRequest = new ChatRequest();
//        chatRequest.setModel("deepseek-chat");
        chatRequest.setModel("deepseek-reasoner");
//        chatRequest.setModel("deepseek-r1:8b");
        chatRequest.setMessages(Collections.singletonList(ChatMessage.newUserMessage("你好呀～")));

        // 测试普通模式
        ChatClientTestUtils.testChatCompletions(chatClient, chatRequest);

        // 测试流式模式
        ChatClientTestUtils.testStreamChatCompletions(chatClient, chatRequest);
    }

}
