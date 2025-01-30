package fun.fengwk.chatjava.core.client.tool;

import fun.fengwk.chatjava.core.client.request.ChatTool;
import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ToolFunctionHandlerRegistry子视图，不支持注册和注销操作
 *
 * @author fengwk
 */
public class ToolFunctionHandlerRegistrySubView implements ToolFunctionHandlerRegistryView {

    private final ToolFunctionHandlerRegistry delegate;
    private final Set<String> names;

    public ToolFunctionHandlerRegistrySubView(ToolFunctionHandlerRegistry delegate, Collection<String> names) {
        this.delegate = Objects.requireNonNull(delegate);
        this.names = new HashSet<>(ChatMiscUtils.nullSafe(names));
    }

    @Override
    public List<ChatTool> getTools() {
        return delegate.getTools().stream()
            .filter(t -> names.contains(t .getFunction().getName()))
            .collect(Collectors.toList());
    }

    @Override
    public ToolFunctionHandler getHandler(String name) {
        if (!names.contains(name)) {
            return null;
        }
        return delegate.getHandler(name);
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
        HashSet<String> allNames = new HashSet<>(names);
        Set<String> delegateNames = delegate.getAllNames();
        allNames.retainAll(delegateNames);
        return allNames;
    }

}
