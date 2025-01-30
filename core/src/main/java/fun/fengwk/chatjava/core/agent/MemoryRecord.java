package fun.fengwk.chatjava.core.agent;

import lombok.Data;

/**
 * 记忆记录
 *
 * @author fengwk
 */
@Data
public class MemoryRecord {

    /**
     * 所属记忆会话id
     */
    private String sessionId;

    /**
     * 所属记忆会话中的序列号
     */
    private Long sequenceNo;

    /**
     * 发言者
     */
    private String speaker;

    /**
     * 发言者角色
     */
    private String speakerRole;

    /**
     * 内容
     */
    private String content;

    /**
     * token数量
     */
    private Integer tokenCount;

}