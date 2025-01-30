package fun.fengwk.chatjava.core.client.util.httpclient;

import java.net.Proxy;
import java.util.List;

/**
 * @author fengwk
 */
public interface ListableProxies {

    /**
     * 列出代理列表
     *
     * @return 代理列表
     */
    List<Proxy> listProxies();

}
