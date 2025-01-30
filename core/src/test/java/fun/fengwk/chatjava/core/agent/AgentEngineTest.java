package fun.fengwk.chatjava.core.agent;

import fun.fengwk.chatjava.core.client.ChatClientProvider;
import fun.fengwk.chatjava.core.client.DefaultChatClientProvider;
import fun.fengwk.chatjava.core.client.token.SimpleDeepSeekChatTokenizer;
import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author fengwk
 */
public class AgentEngineTest {

    @Test
    public void test() {
        // 初始化引擎
        ChatClientProvider chatClientProvider = new DefaultChatClientProvider();
        AgentEngine agentEngine = new ChatClientAgentEngine(chatClientProvider, new SimpleDeepSeekChatTokenizer());

        // 构建Agent
        Agent agent = new Agent();
        agent.setName("小明");
        agent.setPrompt("你是一个资深的律师，你需要为下面我所提出的法律问题做专业的解答");
//        agent.setModel("deepseek-reasoner");
        agent.setModel("deepseek-chat");
        // https://api-docs.deepseek.com/zh-cn/quick_start/parameter_settings
        ChatModelParameters parameters = new ChatModelParameters();
        parameters.setTemperature(1.3f);
        parameters.setTopP(1f);
        parameters.setPresencePenalty(0f);
        parameters.setFrequencyPenalty(0f);
        agent.setModelParameters(parameters.asMap());
        agent.setTools(Collections.emptySet());
        // https://api-docs.deepseek.com/zh-cn/quick_start/pricing
        agent.setContextWindow(1024 * 64);
        agent.setMaxTokens(1024 * 8);

        // 构建MemorySession
        MemorySession memorySession = new InJvmMemorySession();

        // 询问Agent
        Question q1 = new Question();
        q1.setContent("请问直行的汽车撞到转弯的自行车是谁的责任？");
        Answer ans1 = agentEngine.ask(agent, q1, memorySession);
        Assertions.assertNotNull(ans1);
        Assertions.assertNotNull(ans1.getAgent());
        Assertions.assertNotNull(ans1.getContent());
        Assertions.assertFalse(ans1.getContent().isEmpty());

        Question q2 = new Question();
        q2.setContent("简单总结下");
        Answer ans2 = agentEngine.ask(agent, q2, memorySession);
        Assertions.assertNotNull(ans2);
        Assertions.assertNotNull(ans2.getAgent());
        Assertions.assertNotNull(ans2.getContent());
        Assertions.assertFalse(ans2.getContent().isEmpty());

        List<MemoryRecord> memorySequence = memorySession.getAllMemorySequence();
        for (MemoryRecord record : memorySequence) {
            System.out.println("=====================================");
            System.out.println(record.getSpeaker());
            System.out.println(record.getContent());
        }
    }

    @Test
    public void testStream() throws ExecutionException, InterruptedException {
        // 初始化引擎
        ChatClientProvider chatClientProvider = new DefaultChatClientProvider();
        AgentEngine agentEngine = new ChatClientAgentEngine(chatClientProvider, new SimpleDeepSeekChatTokenizer());

        // 构建Agent
        Agent agent = new Agent();
        agent.setName("小明");
        agent.setPrompt("你是一个资深的律师，你需要为下面我所提出的法律问题做专业的解答");
//        agent.setModel("deepseek-reasoner");
        agent.setModel("deepseek-chat");
        // https://api-docs.deepseek.com/zh-cn/quick_start/parameter_settings
        ChatModelParameters parameters = new ChatModelParameters();
        parameters.setTemperature(1.3f);
        parameters.setTopP(1f);
        parameters.setPresencePenalty(0f);
        parameters.setFrequencyPenalty(0f);
        agent.setModelParameters(parameters.asMap());
        agent.setTools(Collections.emptySet());
        // https://api-docs.deepseek.com/zh-cn/quick_start/pricing
        agent.setContextWindow(1024 * 64);
        agent.setMaxTokens(1024 * 8);

        // 构建MemorySession
        MemorySession memorySession = new InJvmMemorySession();

        // 询问Agent
        Question q1 = new Question();
        q1.setContent("请问直行的汽车撞到转弯的自行车是谁的责任？");
        CompletableFuture<Answer> ans1Future = agentEngine.streamAsk(agent, q1, memorySession, new AnswerListener() {
            @Override
            public void onReceive(Answer answer) {
                System.out.print(answer.getContent());
            }
        });
        Answer ans1 = ans1Future.get();
        Assertions.assertNotNull(ans1);
        Assertions.assertNotNull(ans1.getAgent());
        Assertions.assertNotNull(ans1.getContent());
        Assertions.assertFalse(ans1.getContent().isEmpty());

        Question q2 = new Question();
        q2.setContent("简单总结下");
        CompletableFuture<Answer> ans2Future = agentEngine.streamAsk(agent, q2, memorySession, new AnswerListener() {
            @Override
            public void onReceive(Answer answer) {
                System.out.print(answer.getContent());
            }
        });
        Answer ans2 = ans2Future.get();
        Assertions.assertNotNull(ans2);
        Assertions.assertNotNull(ans2.getAgent());
        Assertions.assertNotNull(ans2.getContent());
        Assertions.assertFalse(ans2.getContent().isEmpty());

        List<MemoryRecord> memorySequence = memorySession.getAllMemorySequence();
        for (MemoryRecord record : memorySequence) {
            System.out.println("=====================================");
            System.out.println(record.getSpeakerRole() + "[" + ChatMiscUtils.nullSafe(record.getSpeaker()) + "]");
            System.out.println(record.getContent());
        }
    }

}
