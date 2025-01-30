package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.tool.DefaultToolFunctionHandlerRegistry;
import fun.fengwk.chatjava.core.client.tool.ToolChatClient;
import fun.fengwk.chatjava.core.client.tool.ToolFunctionHandlerRegistry;
import fun.fengwk.chatjava.core.client.tool.ToolFunctionHandlerRegistrySubView;
import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;

import java.net.http.HttpClient;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * @author fengwk
 */
public abstract class AbstractChatClientProvider implements ChatClientProvider {

    private final ToolFunctionHandlerRegistry toolFunctionHandlerRegistry;

    public AbstractChatClientProvider() {
        this(new DefaultToolFunctionHandlerRegistry());
    }

    public AbstractChatClientProvider(ToolFunctionHandlerRegistry toolFunctionHandlerRegistry) {
        this.toolFunctionHandlerRegistry = Objects.requireNonNull(toolFunctionHandlerRegistry);
    }

    public ToolFunctionHandlerRegistry getToolFunctionHandlerRegistry() {
        return toolFunctionHandlerRegistry;
    }

    protected abstract ChatClientOptions getDefaultChatClientOptions();

    protected abstract HttpClient getHttpClient();

    @Override
    public ChatClient getChatClient() {
        return getChatClient(getDefaultChatClientOptions());
    }

    @Override
    public ChatClient getChatClient(ChatClientOptions clientOptions) {
        return new HttpClientChatClient(clientOptions, getHttpClient());
    }

    @Override
    public ChatClient getChatClientWithTools() {
        return getChatClientWithTools(getSupportTools(), getDefaultChatClientOptions());
    }

    @Override
    public ChatClient getChatClientWithTools(Collection<String> tools) {
        return getChatClientWithTools(tools, getDefaultChatClientOptions());
    }

    @Override
    public ChatClient getChatClientWithTools(Collection<String> tools, ChatClientOptions clientOptions) {
        if (ChatMiscUtils.isEmpty(tools)) {
            return getChatClient(clientOptions);
        }
        ChatClient chatGptClient = new HttpClientChatClient(clientOptions, getHttpClient());
        chatGptClient = new ToolChatClient(chatGptClient,
            new ToolFunctionHandlerRegistrySubView(toolFunctionHandlerRegistry, tools));
        return chatGptClient;
    }

    @Override
    public Set<String> getSupportTools() {
        return toolFunctionHandlerRegistry.getAllNames();
    }

}
