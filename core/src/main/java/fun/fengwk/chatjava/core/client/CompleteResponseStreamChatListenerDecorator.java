package fun.fengwk.chatjava.core.client;

/**
 * @author fengwk
 */
public class CompleteResponseStreamChatListenerDecorator extends CompleteResponseStreamChatListener {

    protected final StreamChatListener delegate;

    public CompleteResponseStreamChatListenerDecorator(StreamChatListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onReceive(ChatCompletionsResponse response) {
        delegate.onReceive(response);
        super.onReceive(response);
    }

    @Override
    public void onError(Throwable throwable) {
        delegate.onError(throwable);
        super.onError(throwable);
    }

    @Override
    public void onComplete() {
        delegate.onComplete();
        super.onComplete();
    }

}
