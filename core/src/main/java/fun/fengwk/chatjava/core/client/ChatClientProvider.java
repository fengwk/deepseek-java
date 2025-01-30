package fun.fengwk.chatjava.core.client;

import java.util.Collection;
import java.util.Set;

/**
 * @author fengwk
 */
public interface ChatClientProvider {

    /**
     * 获取ChatClient
     *
     * @return ChatClient
     */
    ChatClient getChatClient();

    /**
     * 获取ChatClient
     *
     * @param clientOptions ClientOptions
     * @return ChatClient
     */
    ChatClient getChatClient(ChatClientOptions clientOptions);

    /**
     * 获取包含所有工具集的ChatClient
     *
     * @return ChatClient
     */
    ChatClient getChatClientWithTools();

    /**
     * 通过toolGroup获取包含工具集的ChatClient
     *
     * @param tools 指定要使用的工具集子集
     * @return ChatClient
     */
    ChatClient getChatClientWithTools(Collection<String> tools);

    /**
     * 通过toolGroup获取包含工具集的ChatClient
     *
     * @param tools 指定要使用的工具集子集
     * @param clientOptions ClientOptions
     * @return ChatClient
     */
    ChatClient getChatClientWithTools(Collection<String> tools, ChatClientOptions clientOptions);

    /**
     * 获取支持的工具名称集合
     *
     * @return 支持的工具名称集合
     */
    Set<String> getSupportTools();

}
