package fun.fengwk.chatjava.core.client.util.httpclient;

import java.net.Proxy;

/**
 * @author fengwk
 */
public interface ConfigurableListableProxies extends ListableProxies {

    /**
     * 添加代理
     *
     * @param proxy 代理
     */
    void addProxy(Proxy proxy);

    /**
     * 移除代理
     *
     * @param proxy 代理
     */
    void removeProxy(Proxy proxy);

}
