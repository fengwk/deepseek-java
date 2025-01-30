package fun.fengwk.chatjava.core.client.tool;

import fun.fengwk.chatjava.core.client.request.ChatTool;

import java.util.List;
import java.util.Set;

/**
 * @author fengwk
 */
public interface ToolFunctionHandlerRegistryView {

    /**
     * 获取当前注册表中的所有工具
     *
     * @return tools
     */
    List<ChatTool> getTools();

    /**
     * 获取指定名称的ToolFunctionHandler
     *
     * @param name 处理器名称
     * @return ToolFunctionHandler
     */
    ToolFunctionHandler getHandler(String name);

    /**
     * 获取指定名称的ToolFunctionHandler
     *
     * @param name 处理器名称
     * @return ToolFunctionHandler
     * @throws IllegalStateException 如果不存在指定名称的handler将抛出该异常
     */
    ToolFunctionHandler getHandlerRequired(String name);

    /**
     * 获取所有注册的处理器名称
     *
     * @return 所有注册的处理器名称
     */
    Set<String> getAllNames();

}
