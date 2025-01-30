package fun.fengwk.chatjava.starter.client;

import fun.fengwk.chatjava.core.client.ChatClientProvider;
import fun.fengwk.chatjava.core.client.tool.DefaultToolFunctionHandlerRegistry;
import fun.fengwk.chatjava.core.client.tool.ToolFunctionHandlerRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
@EnableConfigurationProperties(ChatClientProperties.class)
@AutoConfiguration
public class ChatClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public ToolFunctionHandlerRegistry toolFunctionHandlerRegistry() {
        return new DefaultToolFunctionHandlerRegistry();
    }

    @Bean
    public ToolFunctionPostprocessor toolFunctionPostprocessor(ToolFunctionHandlerRegistry toolFunctionHandlerRegistry) {
        return new ToolFunctionPostprocessor(toolFunctionHandlerRegistry);
    }

    @ConditionalOnMissingBean
    @Bean
    public ChatClientProvider chatClientProvider(ToolFunctionHandlerRegistry toolFunctionHandlerRegistry,
                                                        ChatClientProperties chatClientProperties) {
        return new SpringChatClientProvider(toolFunctionHandlerRegistry, chatClientProperties);
    }

}
