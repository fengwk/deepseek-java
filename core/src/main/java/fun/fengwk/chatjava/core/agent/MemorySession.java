package fun.fengwk.chatjava.core.agent;

import java.util.List;

/**
 * 记忆会话
 *
 * @author fengwk
 */
public interface MemorySession {

    /**
     * 获取记忆系列标识
     */
    String getSessionId();

    /**
     * 将新的记忆内容加入到会话
     *
     * @param speaker 发言者
     * @param speakerRole 发言者权限
     * @param content 内容
     * @param tokenCount token数量
     * @return 追加的新记忆记录
     */
    MemoryRecord append(String speaker, String speakerRole, String content, int tokenCount);

    /**
     * 获取最近的记忆记录列表
     *
     * @param maxTokens 最大token限制
     * @return 最近的记忆序列
     */
    List<MemoryRecord> getLatestMemorySequence(int maxTokens);

    /**
     * 获取所有的记忆记录列表
     *
     * @return 所有的记忆序列
     */
    List<MemoryRecord> getAllMemorySequence();

}
