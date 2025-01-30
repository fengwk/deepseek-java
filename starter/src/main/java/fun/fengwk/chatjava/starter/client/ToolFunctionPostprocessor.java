package fun.fengwk.chatjava.starter.client;

import fun.fengwk.chatjava.core.client.tool.ToolFunctionHandlerRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Objects;

/**
 * @author fengwk
 */
public class ToolFunctionPostprocessor implements BeanPostProcessor {

    private final ToolFunctionHandlerRegistry toolFunctionHandlerRegistry;

    public ToolFunctionPostprocessor(ToolFunctionHandlerRegistry toolFunctionHandlerRegistry) {
        this.toolFunctionHandlerRegistry = Objects.requireNonNull(toolFunctionHandlerRegistry);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        toolFunctionHandlerRegistry.registerBeanIfNecessary(bean);
        return bean;
    }

}
