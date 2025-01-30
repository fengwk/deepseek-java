package fun.fengwk.chatjava.core.agent;

import lombok.Data;

/**
 * 回答
 *
 * @author fengwk
 */
@Data
public class Answer {

    /**
     * 回答的Agent
     */
    private Agent agent;

    /**
     * 回答内容
     */
    private String content;

}
