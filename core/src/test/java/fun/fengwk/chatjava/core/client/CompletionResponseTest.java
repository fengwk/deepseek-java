package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.response.ChatResponse;
import fun.fengwk.chatjava.core.client.util.json.ChatJsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author fengwk
 */
public class CompletionResponseTest {

    @Test
    public void testFromJson() {
        String json = "{\"id\":\"chatcmpl-99agRBLbnTd3U3fA8kYURM1qqLuUv\",\"object\":\"chat.completion\",\"created\":1712072991,\"model\":\"gpt-3.5-turbo-0613\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"你好！有什么我可以帮助你的吗？\"},\"logprobs\":null,\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":9,\"completion_tokens\":18,\"total_tokens\":27},\"system_fingerprint\":null}";
        ChatResponse completionResponse = ChatJsonUtils.fromJson(json, ChatResponse.class);
        assertNotNull(completionResponse);
    }

}
