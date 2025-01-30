package fun.fengwk.chatjava.core.client.util;

import fun.fengwk.chatjava.core.client.ChatCompletionsResponse;
import fun.fengwk.chatjava.core.client.request.ChatMessage;
import fun.fengwk.chatjava.core.client.request.ChatRequest;
import fun.fengwk.chatjava.core.client.request.ChatStreamOptions;
import fun.fengwk.chatjava.core.client.request.ChatTool;
import fun.fengwk.chatjava.core.client.response.ChatChoice;
import fun.fengwk.chatjava.core.client.response.ChatResponse;
import fun.fengwk.chatjava.core.client.response.ChatToolCall;

import java.util.List;

/**
 * @author fengwk
 */
public class ChatUtils {

    private static final String TOOL_CALLS = "tool_calls";
    private static final String FUNCTION = "function";

    private ChatUtils() {}

    public static void streamIncludeUsage(ChatRequest chatRequest) {
        ChatStreamOptions streamOptions = new ChatStreamOptions();
        streamOptions.setInclude_usage(true);
        chatRequest.setStream_options(streamOptions);
    }

    public static ChatMessage getMessage(ChatResponse chatResponse) {
        if (chatResponse == null || ChatMiscUtils.isEmpty(chatResponse.getChoices())
            || chatResponse.getChoices().get(0) == null) {
            return null;
        }

        ChatChoice chatChoice = chatResponse.getChoices().get(0);
        if (chatChoice.getMessage() != null) {
            return chatChoice.getMessage();
        } else {
            return chatChoice.getDelta();
        }
    }

    public static String getContent(ChatResponse chatResponse) {
        if (chatResponse == null || ChatMiscUtils.isEmpty(chatResponse.getChoices())
            || chatResponse.getChoices().get(0) == null) {
            return ChatMiscUtils.EMPTY;
        }

        ChatChoice chatChoice = chatResponse.getChoices().get(0);
        if (chatChoice.getMessage() != null) {
            // 普通数据获取的方式
            return ChatMiscUtils.nullSafe(chatChoice.getMessage().getContent());
        } else if (chatChoice.getDelta() != null) {
            // 流式数据获取的方式
            return ChatMiscUtils.nullSafe(chatChoice.getDelta().getContent());
        }
        return ChatMiscUtils.EMPTY;
    }

    public static boolean isFunctionCall(ChatToolCall call) {
        return call != null && FUNCTION.equals(call.getType());
    }

    public static void setFunctionTool(ChatTool tool) {
        if (tool != null) {
            tool.setType(FUNCTION);
        }
    }

    public static boolean isEmptyDelta(ChatCompletionsResponse response) {
        if (!response.isSuccess()) {
            return false;
        }

        List<ChatChoice> choices = response.getChatResponse().getChoices();
        if (ChatMiscUtils.isEmpty(choices)) {
            return false;
        }

        ChatMessage delta = choices.get(0).getDelta();
        return delta != null && ChatMiscUtils.isEmpty(delta.getContent())
            && ChatMiscUtils.isEmpty(delta.getTool_calls());
    }

    public static boolean isToolCalls(ChatCompletionsResponse response) {
        if (!response.isSuccess()) {
            return false;
        }

        List<ChatChoice> choices = response.getChatResponse().getChoices();
        if (ChatMiscUtils.isEmpty(choices)) {
            return false;
        }

        // 对于普通的响应可以通过Finish_reason检查
        if (TOOL_CALLS.equals(choices.get(0).getFinish_reason())) {
            return true;
        }

        // 对于流式响应需要检查是否有tool_calls
        ChatMessage delta = choices.get(0).getDelta();
        return delta != null && !ChatMiscUtils.isEmpty(delta.getTool_calls());
    }

}
