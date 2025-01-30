package fun.fengwk.chatjava.core.client.tool;

import fun.fengwk.chatjava.core.client.ChatCompletionsResponse;
import fun.fengwk.chatjava.core.client.StreamChatListener;
import fun.fengwk.chatjava.core.client.util.ChatUtils;
import lombok.Getter;

import java.util.LinkedList;

/**
 * 对用户缓冲区进行包装，用户无需监听到function内容
 *
 * @author fengwk
 */
public class ToolStreamChatListener implements StreamChatListener {

    /**
     * 如果是增量消息，且空消息缓冲区为空，无法判断当前消息是否是function，因此需要先暂存
     */
    private final LinkedList<ChatCompletionsResponse> emptyDeltaResponseBuffer = new LinkedList<>();

    private final StreamChatListener delegate;
    @Getter
    private volatile boolean function;

    public ToolStreamChatListener(StreamChatListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onReceive(ChatCompletionsResponse response) {
        if (ChatUtils.isEmptyDelta(response)) {
            emptyDeltaResponseBuffer.offer(response);
        } else {
            if (function || ChatUtils.isToolCalls(response)) {
                function = true;
            } else {
                consumeLeftBuffer();
                delegate.onReceive(response);
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (!function) {
            consumeLeftBuffer();
            delegate.onError(throwable);
        }
    }

    @Override
    public void onComplete() {
        if (!function) {
            consumeLeftBuffer();
            delegate.onComplete();
        }
    }

    private void consumeLeftBuffer() {
        while (!emptyDeltaResponseBuffer.isEmpty()) {
            delegate.onReceive(emptyDeltaResponseBuffer.poll());
        }
    }

}
