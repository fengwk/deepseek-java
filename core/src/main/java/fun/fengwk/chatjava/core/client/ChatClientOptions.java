package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.constant.ChatUrls;
import fun.fengwk.chatjava.core.client.util.httpclient.ChatHttpClientFactory;
import lombok.Data;

import java.net.URI;
import java.time.Duration;

/**
 * @author fengwk
 */
@Data
public class ChatClientOptions {

    /**
     * 补全url
     */
    private URI chatCompletionsUrl = ChatUrls.DEEPSEEK_CHAT_COMPLETIONS;

    /**
     * deekseek token，默认使用环境变量CHAT_API_KEY
     */
    private String token = System.getenv("CHAT_API_KEY");

    /**
     * 每次请求超时
     */
    private Duration perHttpRequestTimeout = ChatHttpClientFactory.getDefaultTimeout();

    /**
     * 为了避免deepseek现有的function calling bug不断循环调用消耗token，需要设置一个循环调用的上限次数
     */
    private int maximumIterations = 5;

}