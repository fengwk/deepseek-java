package fun.fengwk.chatjava.core.client.tool;

import fun.fengwk.chatjava.core.client.ChatClient;
import fun.fengwk.chatjava.core.client.ChatClientOptions;
import fun.fengwk.chatjava.core.client.ChatCompletionsResponse;
import fun.fengwk.chatjava.core.client.StreamChatListener;
import fun.fengwk.chatjava.core.client.request.ChatMessage;
import fun.fengwk.chatjava.core.client.request.ChatRequest;
import fun.fengwk.chatjava.core.client.response.ChatToolCall;
import fun.fengwk.chatjava.core.client.response.ChatToolCallFunction;
import fun.fengwk.chatjava.core.client.util.ChatUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author fengwk
 */
@Slf4j
public class ToolChatClient implements ChatClient {

    private final ChatClient delegate;
    private final ToolFunctionHandlerRegistryView registryView;

    public ToolChatClient(ChatClient delegate, ToolFunctionHandlerRegistryView registryView) {
        this.delegate = Objects.requireNonNull(delegate);
        this.registryView = registryView;
    }

    @Override
    public ChatClientOptions getClientOptions() {
        return delegate.getClientOptions();
    }

    @Override
    public void setClientOptions(ChatClientOptions clientOptions) {
        delegate.setClientOptions(clientOptions);
    }

    @Override
    public ChatCompletionsResponse chatCompletions(ChatRequest chatRequest, ChatClientOptions clientOptions) {
        chatRequest = chatRequest.copy();
        setTools(chatRequest);
        ChatCompletionsResponse response = delegate.chatCompletions(chatRequest, clientOptions);
        int callTimes = 1;
        while (ChatUtils.isToolCalls(response)) {
            if (callTimes > clientOptions.getMaxFunctionCallTimes()) {
                response = handleExceedsCallTimes(response, clientOptions.getMaxFunctionCallTimes());
                break;
            }
            ChatMessage message = response.getChatResponse().getChoices().get(0).getMessage();
            chatRequest.getMessages().add(message);
            List<ChatToolCall> toolCalls = message.getTool_calls();
            for (ChatToolCall toolCall : toolCalls) {
                if (ChatUtils.isFunctionCall(toolCall)) {
                    ChatToolCallFunction function = toolCall.getFunction();
                    ToolFunctionHandler handler = registryView.getHandlerRequired(function.getName());
                    String result = handler.call(function.getArguments());
                    chatRequest.getMessages().add(ChatMessage.newToolMessage(toolCall.getId(), result));
                }
            }
            response = delegate.chatCompletions(chatRequest, clientOptions);
        }
        return response;
    }

    @Override
    public CompletableFuture<ChatCompletionsResponse> streamChatCompletions(
        ChatRequest chatRequest, StreamChatListener chatListener, ChatClientOptions clientOptions) {
        chatRequest = chatRequest.copy();
        setTools(chatRequest);
        return doStreamChatCompletions(chatRequest, chatListener, clientOptions, 1);
    }

    private CompletableFuture<ChatCompletionsResponse> doStreamChatCompletions(
        ChatRequest chatRequest, StreamChatListener chatListener, ChatClientOptions clientOptions, int callTimes) {

        CompletableFuture<ChatCompletionsResponse> future = delegate.streamChatCompletions(
            chatRequest, new ToolStreamChatListener(chatListener), clientOptions);
        return future.thenCompose(resp -> {
            // 不是function调用直接返回
            if (!ChatUtils.isToolCalls(resp)) {
                return CompletableFuture.completedStage(resp);
            }

            // 处理错误情况
            if (!resp.isSuccess()) {
                chatListener.onError(resp.getError());
                return CompletableFuture.completedStage(resp);
            }

            // 处理超出调用限制的情况
            if (callTimes > clientOptions.getMaxFunctionCallTimes())  {
                resp = handleExceedsCallTimes(resp, clientOptions.getMaxFunctionCallTimes());
                chatListener.onError(resp.getError());
                return CompletableFuture.completedStage(resp);
            }

            // 处理function调用
            ChatMessage delta = resp.getChatResponse().getChoices().get(0).getDelta();
            chatRequest.getMessages().add(delta);
            List<ChatToolCall> toolCalls = delta.getTool_calls();
            for (ChatToolCall toolCall : toolCalls) {
                if (ChatUtils.isFunctionCall(toolCall)) {
                    ChatToolCallFunction function = toolCall.getFunction();
                    ToolFunctionHandler handler = registryView.getHandlerRequired(function.getName());
                    String result = handler.call(function.getArguments());
                    chatRequest.getMessages().add(ChatMessage.newToolMessage(toolCall.getId(), result));
                }
            }
            // 重新发起调用
            return doStreamChatCompletions(chatRequest, chatListener, clientOptions, callTimes + 1);
        });
    }

    private void setTools(ChatRequest chatRequest) {
        chatRequest.setTools(registryView.getTools());
    }

    private ChatCompletionsResponse handleExceedsCallTimes(ChatCompletionsResponse resp, int maxFunctionCallTimes) {
        IllegalStateException ex = new IllegalStateException(
            String.format("The number of calls exceeds %d times", maxFunctionCallTimes));
        log.warn("{}, resp: {}", ex.getMessage(), resp);
        return new ChatCompletionsResponse(false, resp.getChatResponse(), ex.getMessage(), ex);
    }

}
