package fun.fengwk.chatjava.starter.client;

import fun.fengwk.chatjava.core.client.ChatClientOptions;
import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;
import fun.fengwk.chatjava.core.client.util.httpclient.ImmutableListableProxies;
import fun.fengwk.chatjava.core.client.util.httpclient.ProxyBuilder;
import fun.fengwk.chatjava.core.client.util.httpclient.ProxySelectorAdapter;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;

import java.net.Proxy;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Data
@ConfigurationProperties("chat.client")
public class ChatClientProperties implements InitializingBean {

    /**
     * 代理配置列表，host:port格式
     */
    private List<ProxyBuilder> proxies = Collections.emptyList();

    /**
     * 连接超时
     */
    private Duration connectTimeout = Duration.ofSeconds(5);

    /**
     * 默认的客户端配置
     */
    private ChatClientOptions defaultChatClientOptions = new ChatClientOptions();

    private volatile HttpClient httpClientCache;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void afterPropertiesSet() {
        this.httpClientCache = null;
    }

    public HttpClient getHttpClient() {
        if (httpClientCache != null) {
            return httpClientCache;
        }
        synchronized (this) {
            if (httpClientCache != null) {
                return httpClientCache;
            }
            List<Proxy> proxies = ChatMiscUtils.nullSafe(getProxies()).stream()
                .map(ProxyBuilder::newProxy).collect(Collectors.toList());
            this.httpClientCache = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(getConnectTimeout())
                .proxy(new ProxySelectorAdapter(new ImmutableListableProxies(proxies)))
                .build();
            return httpClientCache;
        }
    }

}
