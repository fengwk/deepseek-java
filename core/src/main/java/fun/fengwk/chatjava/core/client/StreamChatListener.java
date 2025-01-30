package fun.fengwk.chatjava.core.client;

/**
 * @author fengwk
 */
public interface StreamChatListener {

    /**
     * 每次接收到响应时被调用
     *
     * @param response ChatCompletions响应
     */
    default void onReceive(ChatCompletionsResponse response) {}

    /**
     * 当发生错误时被调用
     *
     * @param throwable 异常
     */
    default void onError(Throwable throwable) {}

    /**
     * 当所有响应接收完毕时被调用
     */
    default void onComplete() {}

}
