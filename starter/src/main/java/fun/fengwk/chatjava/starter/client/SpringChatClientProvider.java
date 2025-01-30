package fun.fengwk.chatjava.starter.client;

import fun.fengwk.chatjava.core.client.AbstractChatClientProvider;
import fun.fengwk.chatjava.core.client.ChatClientOptions;
import fun.fengwk.chatjava.core.client.tool.ToolFunctionHandlerRegistry;

import java.net.http.HttpClient;

/**
 * @author fengwk
 */
public class SpringChatClientProvider extends AbstractChatClientProvider {

    private final ChatClientProperties chatClientProperties;

    public SpringChatClientProvider(ToolFunctionHandlerRegistry toolFunctionHandlerRegistry,
                                    ChatClientProperties chatClientProperties) {
        super(toolFunctionHandlerRegistry);
        this.chatClientProperties = chatClientProperties;
    }

    @Override
    protected ChatClientOptions getDefaultChatClientOptions() {
        return chatClientProperties.getDefaultChatClientOptions();
    }

    @Override
    protected HttpClient getHttpClient() {
        return chatClientProperties.getHttpClient();
    }

}
