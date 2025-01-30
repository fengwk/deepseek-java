package fun.fengwk.chatjava.core.agent;

import fun.fengwk.chatjava.core.client.ChatCompletionsResponse;
import fun.fengwk.chatjava.core.client.StreamChatListener;
import fun.fengwk.chatjava.core.client.util.ChatUtils;

import java.util.Objects;

/**
 * @author fengwk
 */
public class AnswerListenerAdapter implements StreamChatListener {

    private final Agent agent;
    private final AnswerListener answerListener;

    public AnswerListenerAdapter(Agent agent, AnswerListener answerListener) {
        this.agent = Objects.requireNonNull(agent);
        this.answerListener = Objects.requireNonNull(answerListener);
    }

    @Override
    public void onReceive(ChatCompletionsResponse response) {
        if (response.isSuccess()) {
            Answer answer = new Answer();
            answer.setAgent(agent);
            answer.setContent(ChatUtils.getContent(response.getChatResponse()));
            answerListener.onReceive(answer);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        answerListener.onError(throwable);
    }

    @Override
    public void onComplete() {
        answerListener.onComplete();
    }

}
