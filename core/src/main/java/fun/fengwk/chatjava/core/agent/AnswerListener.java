package fun.fengwk.chatjava.core.agent;

/**
 * @author fengwk
 */
public interface AnswerListener {

    /**
     * 每次接收到Answer时被调用
     *
     * @param answer 接受到Answer
     */
    default void onReceive(Answer answer) {}

    /**
     * 当发生错误时被调用
     *
     * @param throwable 异常
     */
    default void onError(Throwable throwable) {}

    /**
     * 当所有Answer接收完毕时被调用
     */
    default void onComplete() {}

}
