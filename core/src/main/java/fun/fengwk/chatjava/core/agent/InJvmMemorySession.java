package fun.fengwk.chatjava.core.agent;

import java.util.*;

/**
 * @author fengwk
 */
public class InJvmMemorySession implements MemorySession {

    private final String sessionId;
    private final List<MemoryRecord> records = new ArrayList<>();
    private long seqNoCounter = 0;

    public InJvmMemorySession() {
        this(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    public InJvmMemorySession(String sessionId) {
        this.sessionId = Objects.requireNonNull(sessionId);
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public synchronized MemoryRecord append(String speaker, String speakerRole, String content, int tokenCount) {
        MemoryRecord record = new MemoryRecord();
        record.setSessionId(getSessionId());
        record.setSequenceNo(seqNoCounter++);
        record.setSpeaker(speaker);
        record.setSpeakerRole(speakerRole);
        record.setContent(content);
        record.setTokenCount(tokenCount);
        records.add(record);
        return record;
    }

    @Override
    public synchronized List<MemoryRecord> getLatestMemorySequence(int maxTokens) {
        LinkedList<MemoryRecord> latestMemorySequence = new LinkedList<>();
        for (int i = records.size() - 1; i >= 0; i--) {
            MemoryRecord record = records.get(i);
            if (maxTokens - record.getTokenCount() < 0) {
                break;
            }
            maxTokens -= record.getTokenCount();;
            latestMemorySequence.addFirst(record);
        }
        return latestMemorySequence;
    }

    @Override
    public synchronized List<MemoryRecord> getAllMemorySequence() {
        return Collections.unmodifiableList(records);
    }

}
