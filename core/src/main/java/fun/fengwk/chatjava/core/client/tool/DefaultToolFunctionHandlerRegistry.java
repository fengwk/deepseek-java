package fun.fengwk.chatjava.core.client.tool;

import fun.fengwk.chatjava.core.client.request.ChatTool;
import fun.fengwk.chatjava.core.client.request.ChatToolFunction;
import fun.fengwk.chatjava.core.client.util.ChatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class DefaultToolFunctionHandlerRegistry implements ToolFunctionHandlerRegistry {

    private final ConcurrentMap<String, ToolFunctionHandler> registry = new ConcurrentHashMap<>();

    @Override
    public List<ChatTool> getTools() {
        return registry.values().stream()
            .map(this::convert).collect(Collectors.toList());
    }

    @Override
    public void registerBeanIfNecessary(Object bean) {
        ToolFunctionParser toolFunctionParser = new ToolFunctionParser();
        List<ToolFunctionHandler> handlers = toolFunctionParser.parse(bean);
        handlers.forEach(this::registerHandler);
    }

    @Override
    public void registerHandler(ToolFunctionHandler handler) {
        if (!registerHandlerIfAbsent(handler)) {
            throw new IllegalStateException("duplicate tool function handler: " + handler.getName());
        }
    }

    @Override
    public boolean registerHandlerIfAbsent(ToolFunctionHandler handler) {
        return registry.putIfAbsent(handler.getName(), handler) == null;
    }

    @Override
    public boolean unregisterHandler(String name) {
        return registry.remove(name) != null;
    }

    @Override
    public ToolFunctionHandler getHandler(String name) {
        return registry.get(name);
    }

    @Override
    public ToolFunctionHandler getHandlerRequired(String name) {
        ToolFunctionHandler handler = getHandler(name);
        if (handler == null) {
            throw new IllegalStateException(String.format("handler '%s' not exists", name));
        }
        return handler;
    }

    @Override
    public Set<String> getAllNames() {
        return Collections.unmodifiableSet(registry.keySet());
    }

    private ChatTool convert(ToolFunctionHandler handler) {
        ChatTool chatTool = new ChatTool();
        ChatUtils.setFunctionTool(chatTool);

        ChatToolFunction function = new ChatToolFunction();
        function.setDescription(handler.getDescription());
        function.setName(handler.getName());
        function.setParameters(handler.getParameters());
        chatTool.setFunction(function);

        return chatTool;
    }

}