package fun.fengwk.chatjava.core.client.constant;

import java.net.URI;

/**
 * @author fengwk
 */
public class ChatUrls {

    /**
     * chatgpt
     */
    public static final URI OPENAI_CHAT_COMPLETIONS = URI.create("https://api.openai.com/v1/chat/completions");

    /**
     * deepseek
     */
    public static final URI DEEPSEEK_CHAT_COMPLETIONS = URI.create("https://api.deepseek.com/chat/completions");

    private ChatUrls() {}

}
