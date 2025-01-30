package fun.fengwk.chatjava.core.client.util.httpclient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * @author fengwk
 */
public class ChatHttpClientFactory {

    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);

    private static final ConfigurableListableProxies DEFAULT_LISTABLE_PROXIES = new DefaultConfigurableListableProxies();
    private static final HttpClient DEFAULT_HTTP_CLIENT = newDefaultHttpClient();

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    private ChatHttpClientFactory() {
    }

    private static HttpClient newDefaultHttpClient() {
        return HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT)
            .proxy(new ProxySelectorAdapter(DEFAULT_LISTABLE_PROXIES))
            .build();
    }

    /**
     * 获取默认的超时时间
     *
     * @return 默认的超时时间
     */
    public static Duration getDefaultTimeout() {
        return DEFAULT_TIMEOUT;
    }

    /**
     * 获取默认的HttpClient
     *
     * @return 默认的HttpClient
     */
    public static HttpClient getDefaultHttpClient() {
        return DEFAULT_HTTP_CLIENT;
    }

    /**
     * 获取默认的ConfigurableListableProxies
     *
     * @return 默认的ConfigurableListableProxies
     */
    public static ConfigurableListableProxies getDefaultConfigurableListableProxies() {
        return DEFAULT_LISTABLE_PROXIES;
    }

}
