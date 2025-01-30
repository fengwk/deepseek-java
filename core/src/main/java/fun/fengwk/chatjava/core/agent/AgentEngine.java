package fun.fengwk.chatjava.core.agent;

import java.util.concurrent.CompletableFuture;

/**
 * @author fengwk
 */
public interface AgentEngine {

    /**
     * 询问指定Agent问题
     *
     * @param agent agent
     * @param question question
     * @param memorySession memorySession
     * @return Answer
     * @throws RuntimeException answer failed
     */
    Answer ask(Agent agent, Question question, MemorySession memorySession);

    /**
     * 流式询问指定Agent问题
     *
     * @param agent agent
     * @param question question
     * @param memorySession memorySession
     * @param answerListener answerListener
     * @return Answer CompletableFuture
     */
    CompletableFuture<Answer> streamAsk(Agent agent, Question question, MemorySession memorySession,
                                        AnswerListener answerListener);

}
