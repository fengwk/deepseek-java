package fun.fengwk.chatjava.core.client.response;

import lombok.Data;

import java.util.List;

/**
 * @author fengwk
 */
@Data
public class ChatLogprobs {

    /**
     * A list of message content tokens with log probability information.
     */
    private List<ChatLogprobContent> content;

}
