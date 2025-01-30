package fun.fengwk.chatjava.core.agent;

import fun.fengwk.chatjava.core.client.ChatClient;
import fun.fengwk.chatjava.core.client.ChatClientProvider;
import fun.fengwk.chatjava.core.client.ChatCompletionsResponse;
import fun.fengwk.chatjava.core.client.request.ChatMessage;
import fun.fengwk.chatjava.core.client.request.ChatRequest;
import fun.fengwk.chatjava.core.client.response.ChatResponse;
import fun.fengwk.chatjava.core.client.response.ChatUsage;
import fun.fengwk.chatjava.core.client.token.ChatTokenizer;
import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;
import fun.fengwk.chatjava.core.client.util.ChatUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author fengwk
 */
@Slf4j
public class ChatClientAgentEngine implements AgentEngine {

    private final ChatClientProvider chatClientProvider;
    private final ChatTokenizer chatTokenizer;

    public ChatClientAgentEngine(ChatClientProvider chatClientProvider, ChatTokenizer chatTokenizer) {
        this.chatClientProvider = Objects.requireNonNull(chatClientProvider);
        this.chatTokenizer = chatTokenizer;
    }

    @Override
    public Answer ask(Agent agent, Question question, MemorySession memorySession) {
        checkAgent(agent);
        checkQuestion(question);
        checkMemorySession(memorySession);

        ChatClient chatGptClient = chatClientProvider.getChatClientWithTools(agent.getTools());

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel(agent.getModel());
        chatRequest.setMessages(buildMessages(agent, question, memorySession));
        ChatModelParameters modelParameters = ChatModelParameters.fromMap(agent.getModelParameters());
        setChatGptParameters(chatRequest, modelParameters);

        ChatCompletionsResponse response = chatGptClient.chatCompletions(chatRequest);
        if (!response.isSuccess()) {
            log.error("ask error, agent: {}, question: {}, response: {}", agent, question, response);
            throw response.toRuntimeException();
        }

        ChatResponse chatResponse = response.getChatResponse();
        ChatMessage message = ChatUtils.getMessage(chatResponse);
        ChatUsage usage = chatResponse.getUsage();

        Answer answer = new Answer();
        answer.setAgent(agent);
        answer.setContent(message.getContent());

        // record memory
        memorySession.append(agent.getName(), ChatMessage.ROLE_USER, question.getContent(), usage.getPrompt_tokens());
        memorySession.append(message.getName(), message.getRole(), message.getContent(), usage.getCompletion_tokens());

        return answer;
    }

    @Override
    public CompletableFuture<Answer> streamAsk(Agent agent, Question question, MemorySession memorySession,
                                               AnswerListener answerListener) {
        checkAgent(agent);
        checkQuestion(question);
        checkMemorySession(memorySession);
        checkAnswerListener(answerListener);

        ChatClient chatGptClient = chatClientProvider.getChatClientWithTools(agent.getTools());

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel(agent.getModel());
        chatRequest.setMessages(buildMessages(agent, question, memorySession));
        ChatModelParameters modelParameters = ChatModelParameters.fromMap(agent.getModelParameters());
        if (agent.getMaxTokens() != null) {
            chatRequest.setMax_tokens(agent.getMaxTokens());
        }
        setChatGptParameters(chatRequest, modelParameters);

        return chatGptClient.streamChatCompletions(
                chatRequest, new AnswerListenerAdapter(agent, answerListener))
            .thenApply(response -> {
                if (!response.isSuccess()) {
                    log.error("streamAsk error, agent: {}, question: {}, response: {}", agent, question, response);
                    throw response.toRuntimeException();
                }

                ChatResponse chatResponse = response.getChatResponse();
                ChatMessage message = ChatUtils.getMessage(chatResponse);
                ChatUsage usage = chatResponse.getUsage();

                Answer answer = new Answer();
                answer.setAgent(agent);
                answer.setContent(ChatUtils.getContent(chatResponse));

                // record memory
                memorySession.append(agent.getName(), ChatMessage.ROLE_USER, question.getContent(), usage.getPrompt_tokens());
                memorySession.append(message.getName(), message.getRole(), message.getContent(), usage.getCompletion_tokens());

                return answer;
            });
    }

    private void checkAgent(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent must not be null");
        }
        if (ChatMiscUtils.isEmpty(agent.getModel())) {
            throw new IllegalArgumentException("agent.model must not be empty");
        }
    }

    private void checkQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("question must not be null");
        }
        if (ChatMiscUtils.isEmpty(question.getContent())) {
            throw new IllegalArgumentException("question.content must not be empty");
        }
    }

    private void checkMemorySession(MemorySession memorySession) {
        if (memorySession == null) {
            throw new IllegalArgumentException("memorySession must not be null");
        }
    }

    private void checkAnswerListener(AnswerListener answerListener) {
        if (answerListener == null) {
            throw new IllegalArgumentException("answerListener must not be null");
        }
    }

    private void setChatGptParameters(ChatRequest chatRequest, ChatModelParameters modelParameters) {
        chatRequest.setTemperature(modelParameters.getTemperature());
        chatRequest.setTop_p(modelParameters.getTopP());
        chatRequest.setPresence_penalty(modelParameters.getPresencePenalty());
        chatRequest.setFrequency_penalty(modelParameters.getFrequencyPenalty());
    }

    private List<ChatMessage> buildMessages(Agent agent, Question question, MemorySession memorySession) {
//        if (!SupportModels.supportModel(agent.getModel())) {
//            throw new IllegalArgumentException("not support model: " + agent.getModel());
//        }

        List<ChatMessage> messages = new ArrayList<>();

        // system prompt
        if (ChatMiscUtils.isNotEmpty(agent.getPrompt())) {
            messages.add(ChatMessage.newSystemMessage(agent.getPrompt()));
        }

        // memory
        // TODO 暂时不使用SupportModels进行限制了因为会发生变化，只使用Agent传递的参数进行处理
//        ChatModel chatModel = SupportModels.getModeRequired(agent.getModel());
//        int contextWindow = chatModel.getContextWindow();
//        if (agent.getContextWindow() != null && agent.getContextWindow() < contextWindow) {
//            contextWindow = agent.getContextWindow();
//        }
//        int maxTokens = chatModel.getMaxTokens();
//        if (agent.getMaxTokens() != null && agent.getMaxTokens() < maxTokens) {
//            maxTokens = agent.getMaxTokens();
//        }
//        Integer contextWindow = agent.getContextWindow();
//        Integer maxTokens = agent.getMaxTokens();

        if (!chatTokenizer.support(agent.getModel())) {
            throw new IllegalStateException(String.format("current tokenizer [%s] can not support model: %s",
                chatTokenizer, agent.getModel()));
        }
//        int inferredUsage = chatTokenizer.countTokens(
//            ChatMiscUtils.nullSafe(agent.getPrompt()) + ChatMiscUtils.nullSafe(question.getContent()),
//            agent.getModel());
//        int leftTokens = contextWindow - maxTokens - inferredUsage;
//        List<MemoryRecord> latestMemorySequence = memorySession.getLatestMemorySequence(leftTokens);
        List<MemoryRecord> allMemorySequence = memorySession.getAllMemorySequence();
        for (MemoryRecord memoryRecord : allMemorySequence) {
            if (Objects.equals(memoryRecord.getSpeakerRole(), ChatMessage.ROLE_USER)) {
                messages.add(ChatMessage.newUserMessage(memoryRecord.getContent(), memoryRecord.getSpeaker()));
            } else {
                messages.add(ChatMessage.newAssistantMessage(memoryRecord.getContent()));
            }
        }

        // question
        messages.add(ChatMessage.newUserMessage(question.getContent(), agent.getName()));

        return messages;
    }

}
