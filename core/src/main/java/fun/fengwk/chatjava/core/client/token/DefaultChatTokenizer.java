package fun.fengwk.chatjava.core.client.token;

/**
 * @author fengwk
 */
public class DefaultChatTokenizer implements ChatTokenizer {

    @Override
    public boolean support(String modelName) {
        return true;
    }

    @Override
    public int countTokens(String text, String modelName) {
        return text.length();
    }

}
