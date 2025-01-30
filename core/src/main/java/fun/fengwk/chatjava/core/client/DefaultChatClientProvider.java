package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.tool.ToolFunctionHandlerRegistry;
import fun.fengwk.chatjava.core.client.util.httpclient.ChatHttpClientFactory;

import java.net.http.HttpClient;
import java.util.Objects;

/**
 * @author fengwk
 */
public class DefaultChatClientProvider extends AbstractChatClientProvider {

    private final ChatClientOptions defualtChatClientOptions;
    private final HttpClient httpClient;

    public DefaultChatClientProvider() {
        super();
        this.defualtChatClientOptions = new ChatClientOptions();
        this.httpClient = ChatHttpClientFactory.getDefaultHttpClient();
    }

    public DefaultChatClientProvider(ToolFunctionHandlerRegistry toolFunctionHandlerRegistry,
                                     ChatClientOptions defualtChatClientOptions,
                                     HttpClient httpClient) {
        super(toolFunctionHandlerRegistry);
        this.defualtChatClientOptions = Objects.requireNonNull(defualtChatClientOptions);
        this.httpClient = Objects.requireNonNull(httpClient);
    }

    @Override
    protected ChatClientOptions getDefaultChatClientOptions() {
        return defualtChatClientOptions;
    }

    @Override
    protected HttpClient getHttpClient() {
        return httpClient;
    }

}
