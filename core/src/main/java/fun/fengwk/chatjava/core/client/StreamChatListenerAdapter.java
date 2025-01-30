package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.response.ChatResponse;
import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;
import fun.fengwk.chatjava.core.client.util.json.ChatJsonUtils;

import java.util.Objects;
import java.util.concurrent.Flow;

/**
 * @author fengwk
 */
class StreamChatListenerAdapter implements Flow.Subscriber<String> {

    private static final String KEY_DATA = "data";
    private static final String VALUE_DONE = "[DONE]";

    private final StreamChatListener listener;
    private volatile Flow.Subscription subscription;

    StreamChatListenerAdapter(StreamChatListener listener) {
        this.listener = Objects.requireNonNull(listener);
    }

    @Override
    public synchronized void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public synchronized void onNext(String line) {
        int spIdx;
        if (ChatMiscUtils.isNotEmpty(line) && (spIdx = line.indexOf(':')) >= 0) {
            String key = line.substring(0, spIdx).trim();
            String value = line.substring(spIdx + 1).trim();
            if (KEY_DATA.equals(key)) {
                if (!VALUE_DONE.equals(value)) {
                    try {
                        ChatResponse response = ChatJsonUtils.fromJson(value, ChatResponse.class);
                        listener.onReceive(new ChatCompletionsResponse(true, response, null, null));
                    } catch (Exception ex) {
                        listener.onReceive(new ChatCompletionsResponse(false, null, value, ex));
                    }
                }
            }
            else {
                listener.onReceive(new ChatCompletionsResponse(
                    false, null, value, new IllegalStateException("streamChatCompletions errorMessage")));
            }
        }
        subscription.request(1);
    }

    @Override
    public synchronized void onError(Throwable throwable) {
        listener.onError(throwable);
    }

    @Override
    public synchronized void onComplete() {
        listener.onComplete();
    }

}
