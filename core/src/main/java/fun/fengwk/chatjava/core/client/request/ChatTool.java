package fun.fengwk.chatjava.core.client.request;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ChatTool {

    /**
     * Required, The type of the tool. Currently, only function is supported.
     */
    private String type;

    /**
     * function.
     */
    private ChatToolFunction function;

}
