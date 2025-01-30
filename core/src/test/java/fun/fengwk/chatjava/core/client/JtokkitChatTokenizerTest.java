package fun.fengwk.chatjava.core.client;

import com.knuddels.jtokkit.api.ModelType;
import fun.fengwk.chatjava.core.client.token.JtokkitChatTokenizer;
import org.junit.jupiter.api.Test;

/**
 * @author fengwk
 */
public class JtokkitChatTokenizerTest {

    @Test
    public void test() {
        JtokkitChatTokenizer tokenizer = new JtokkitChatTokenizer();
        int count = tokenizer.countTokens("你是一个好人", ModelType.GPT_3_5_TURBO.getName());
        assert count == 5;
    }

}
