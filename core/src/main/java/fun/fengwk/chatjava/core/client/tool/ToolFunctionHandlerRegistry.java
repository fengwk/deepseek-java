package fun.fengwk.chatjava.core.client.tool;

/**
 * @author fengwk
 */
public interface ToolFunctionHandlerRegistry extends ToolFunctionHandlerRegistryView {

    /**
     * 如果bean对象包含ToolFunctionHandler将进行注册
     *
     * @param bean bean对象
     * @throws IllegalStateException 如果重复注册了同名handler将抛出该异常
     */
    void registerBeanIfNecessary(Object bean);

    /**
     * 注册ToolFunctionHandler
     *
     * @param handler ToolFunctionHandler
     * @throws IllegalStateException 如果重复注册了同名handler将抛出该异常
     */
    void registerHandler(ToolFunctionHandler handler);

    /**
     * 如果不存在同名ToolFunctionHandler则进行注册
     *
     * @param handler ToolFunctionHandler
     */
    boolean registerHandlerIfAbsent(ToolFunctionHandler handler);

    /**
     * 注销指定名称的ToolFunctionHandler
     *
     * @param name 处理器名称
     * @return 是否有ToolFunctionHandler被注销
     */
    boolean unregisterHandler(String name);

}
