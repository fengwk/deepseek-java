package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.request.ChatMessage;
import fun.fengwk.chatjava.core.client.response.ChatChoice;
import fun.fengwk.chatjava.core.client.response.ChatResponse;
import fun.fengwk.chatjava.core.client.response.ChatToolCall;
import fun.fengwk.chatjava.core.client.response.ChatToolCallFunction;
import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;
import lombok.Getter;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class CompleteResponseStreamChatListener implements StreamChatListener {

    @Getter
    protected volatile ChatCompletionsResponse response; // 不成功时会设置response
    @Getter
    protected volatile ChatResponse chatResponse;
    @Getter
    protected volatile Throwable error;
    @Getter
    protected volatile boolean complete;

    public ChatCompletionsResponse toChatCompletionsResponse() {
        if (this.response != null) {
            return this.response;
        } else if (error != null) {
            return new ChatCompletionsResponse(false, null, error.getMessage(), error);
        } else if (complete) {
            if (chatResponse != null) {
                return new ChatCompletionsResponse(true, chatResponse, null, null);
            } else {
                IllegalStateException ex = new IllegalStateException("chatResponse is empty");
                return new ChatCompletionsResponse(false, null, ex.getMessage(),ex);
            }
        } else {
            IllegalStateException ex = new IllegalStateException("chatResponse not complete");
            return new ChatCompletionsResponse(false, null, ex.getMessage(),ex);
        }
    }

    @Override
    public void onReceive(ChatCompletionsResponse response) {
        if (response.isSuccess()) {
            if (this.chatResponse == null) {
                this.chatResponse = response.getChatResponse();
            } else {
                mergeResponse(this.chatResponse, response.getChatResponse());
            }
        } else {
            this.response = response;
        }
    }

    @Override
    public void onError(Throwable throwable) {
        this.error = throwable;
    }

    @Override
    public void onComplete() {
        this.complete = true;
    }

    private void mergeResponse(ChatResponse to, ChatResponse from) {
        if (to == null || from == null) {
            return;
        }

        mergeProperty(to, from, ChatResponse::getId, ChatResponse::setId);
        if (!mergeProperty(to, from, ChatResponse::getChoices, ChatResponse::setChoices)) {
            to.setChoices(mergeChoices(to.getChoices(), from.getChoices()));
        }
        mergeProperty(to, from, ChatResponse::getCreated, ChatResponse::setCreated);
        mergeProperty(to, from, ChatResponse::getModel, ChatResponse::setModel);
        mergeProperty(to, from, ChatResponse::getSystem_fingerprint, ChatResponse::setSystem_fingerprint);
        mergeProperty(to, from, ChatResponse::getObject, ChatResponse::setObject);
        mergeProperty(to, from, ChatResponse::getUsage, ChatResponse::setUsage);
    }

    private List<ChatChoice> mergeChoices(List<ChatChoice> to, List<ChatChoice> from) {
        to = ChatMiscUtils.nullSafe(to, ArrayList::new);
        Set<Integer> toIndexes = to.stream().map(ChatChoice::getIndex).collect(Collectors.toSet());
        Map<Integer, ChatChoice> fromMap = ChatMiscUtils.nullSafe(from).stream()
            .collect(Collectors.toMap(ChatChoice::getIndex, Function.identity()));

        // 如果from有的index，to没有则需要查询
        for (Integer fromIndex : new HashSet<>(fromMap.keySet())) {
            if (!toIndexes.contains(fromIndex)) {
                to.add(fromMap.get(fromIndex));
                fromMap.remove(fromIndex);
            }
        }

        // 合并所有剩余的from到to
        for (int i = 0; i < to.size(); i++) {
            ChatChoice cto = to.get(i);
            ChatChoice cfrom = fromMap.get(cto.getIndex());
            if (cfrom != null) {
                mergeChoice(cto, cfrom);
            }
        }

        // 按照索引排序
        to.sort(Comparator.comparing(ChatChoice::getIndex));

        return to;
    }

    private void mergeChoice(ChatChoice to, ChatChoice from) {
        if (to == null || from == null) {
            return;
        }

        mergeProperty(to, from, ChatChoice::getFinish_reason, ChatChoice::setFinish_reason);
        mergeProperty(to, from, ChatChoice::getIndex, ChatChoice::setIndex);
        if (!mergeProperty(to, from, ChatChoice::getMessage, ChatChoice::setMessage)) {
            mergeMessage(to.getMessage(), from.getMessage());
        }
        if (!mergeProperty(to, from, ChatChoice::getDelta, ChatChoice::setDelta)) {
            mergeMessage(to.getDelta(), from.getDelta());
        }
        mergeProperty(to, from, ChatChoice::getLogprobs, ChatChoice::setLogprobs);
    }

    private void mergeMessage(ChatMessage to, ChatMessage from) {
        if (to == null || from == null) {
            return;
        }

        to.setContent(ChatMiscUtils.nullSafe(to.getContent()) + ChatMiscUtils.nullSafe(from.getContent()));
        to.setReasoning_content(ChatMiscUtils.nullSafe(to.getReasoning_content())
            + ChatMiscUtils.nullSafe(from.getReasoning_content()));
        mergeProperty(to, from, ChatMessage::getRole, ChatMessage::setRole);
        mergeProperty(to, from, ChatMessage::getName, ChatMessage::setName);
        mergeProperty(to, from, ChatMessage::getTool_call_id, ChatMessage::setTool_call_id);
        if (!mergeProperty(to, from, ChatMessage::getTool_calls, ChatMessage::setTool_calls)) {
            to.setTool_calls(mergeToolCalls(to.getTool_calls(), from.getTool_calls()));
        }
        if (!mergeProperty(to, from, ChatMessage::getFunction_call, ChatMessage::setFunction_call)) {
            mergeChatToolCallFunction(to.getFunction_call(), from.getFunction_call());
        }
    }

    private List<ChatToolCall> mergeToolCalls(List<ChatToolCall> to, List<ChatToolCall> from) {
        // deekseek看起来是按照列表顺序来进行生成的，因此也按照列表顺序进行合并
        // TODO 未测试chatgpt api
        to = ChatMiscUtils.nullSafe(to, ArrayList::new);
        from = ChatMiscUtils.nullSafe(from, ArrayList::new);

        for (int i = 0; i < from.size(); i++) {
            ChatToolCall cfrom = from.get(i);
            if (to.isEmpty() || ChatMiscUtils.isNotEmpty(cfrom.getId())) {
                to.add(cfrom);
            } else {
                mergeToolCall(ChatMiscUtils.tryGetLast(to), cfrom);
            }
        }

        return to;
    }

    private void mergeToolCall(ChatToolCall to, ChatToolCall from) {
        if (to == null || from == null) {
            return;
        }

        mergeProperty(to, from, ChatToolCall::getId, ChatToolCall::setId);
        mergeProperty(to, from, ChatToolCall::getType, ChatToolCall::setType);
        if (!mergeProperty(to, from, ChatToolCall::getFunction, ChatToolCall::setFunction)) {
            mergeChatToolCallFunction(to.getFunction(), from.getFunction());
        }
    }

    private void mergeChatToolCallFunction(ChatToolCallFunction to, ChatToolCallFunction from) {
        if (to == null || from == null) {
            return;
        }

        mergeProperty(to, from, ChatToolCallFunction::getName, ChatToolCallFunction::setName);
        to.setArguments(ChatMiscUtils.nullSafe(to.getArguments()) + ChatMiscUtils.nullSafe(from.getArguments()));
    }

    private <T, P> boolean mergeProperty(T to, T from, Function<T, P> getter, BiConsumer<T, P> setter) {
        if (getter.apply(to) == null && getter.apply(from) != null) {
            setter.accept(to, getter.apply(from));
            return true;
        }
        return false;
    }

}
