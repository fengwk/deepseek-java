package fun.fengwk.chatjava.example.cmd;

import fun.fengwk.chatjava.core.agent.*;
import fun.fengwk.chatjava.core.client.ChatClientOptions;
import fun.fengwk.chatjava.core.client.ChatClientProvider;
import fun.fengwk.chatjava.core.client.DefaultChatClientProvider;
import fun.fengwk.chatjava.core.client.token.ChatTokenizer;
import fun.fengwk.chatjava.core.client.token.DefaultChatTokenizer;
import fun.fengwk.chatjava.core.client.token.JtokkitChatTokenizer;
import fun.fengwk.chatjava.core.client.token.SimpleDeepSeekChatTokenizer;
import fun.fengwk.chatjava.core.client.tool.DefaultToolFunctionHandlerRegistry;
import org.apache.commons.cli.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author fengwk
 */
public class Main {

    public static void main(String[] args) throws ParseException, ExecutionException, InterruptedException {
        Options options = new Options();
        options.addOption("h", "help", false, "help");
        options.addOption("p", "prompt", true, "prompt");
        options.addOption("m", "model", true, "model, default is 'deepseek-chat'");
        options.addOption("k", "token", true, "api token, default is \"$CHAT_API_KEY\"");
        options.addOption("u", "uri", true, "api base uri, default is https://api.deepseek.com/chat/completions");
        options.addOption("t", "timeout", true, "per http request timeout, default is 60s");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter formatter = HelpFormatter.builder().get();
            formatter.printHelp("java -jar chat-java-example-cmd-0.0.1-jar-with-dependencies.jar", options);
            return;
        }

        // 构建ChatClientProvider
        ChatClientOptions defualtChatClientOptions = new ChatClientOptions();
        if (cmd.hasOption("u")) {
            defualtChatClientOptions.setChatCompletionsUrl(URI.create(cmd.getOptionValue("u")));
        }
        if (cmd.hasOption("k")) {
            defualtChatClientOptions.setToken(cmd.getOptionValue("k"));
        }
        if (cmd.hasOption("t")) {
            defualtChatClientOptions.setPerHttpRequestTimeout(Duration.ofSeconds(Long.parseLong(cmd.getOptionValue("t"))));
        }
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(defualtChatClientOptions.getPerHttpRequestTimeout())
                .build();
        ChatClientProvider chatClientProvider = new DefaultChatClientProvider(
                new DefaultToolFunctionHandlerRegistry(), defualtChatClientOptions, httpClient);

        // 推断tokenizer
        String model = "deepseek-chat";
        if (cmd.hasOption("m")) {
            model = cmd.getOptionValue("m");
        }
        ChatTokenizer chatTokenizer;
        if (model.toLowerCase().contains("deepseek")) {
            chatTokenizer = new SimpleDeepSeekChatTokenizer();
        } else if (model.toLowerCase().contains("chatgpt")) {
            chatTokenizer = new JtokkitChatTokenizer();
        } else {
            chatTokenizer = new DefaultChatTokenizer();
        }

        // 构建AgentEngine
        AgentEngine agentEngine = new ChatClientAgentEngine(chatClientProvider, chatTokenizer);

        // 构建Agent
        Agent agent = new Agent();
        if (cmd.hasOption("p")) {
            agent.setPrompt(cmd.getOptionValue("p"));
        }
        agent.setModel(model);
//        agent.setModelParameters();
//        agent.setContextWindow();
//        agent.setMaxTokens();

        // 构建存储会话
        MemorySession memorySession = new InJvmMemorySession();

        System.out.println("1. 输入你的问题按下回车键和机器人聊天");
        System.out.flush();
        System.out.println("2. 输入exit退出聊天");
        System.out.flush();

        Scanner scanner = new Scanner(System.in);
        AtomicBoolean running = new AtomicBoolean(true);
        while (running.get()) {
            System.out.println("> 用户");
            System.out.flush();
            String userInput = scanner.nextLine();
            Question q = new Question();
            q.setContent(userInput);

            if ("exit".equals(userInput)) {
                running.set(false);
                continue;
            }

            System.out.println("> AI");
            System.out.flush();

            CompletableFuture<Answer> future = agentEngine.streamAsk(agent, q, memorySession, new AnswerListener() {
                @Override
                public void onReceive(Answer answer) {
                    System.out.print(answer.getContent());
                    System.out.flush();
                }
                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace(System.err);
                    System.err.flush();
                    running.set(false);
                }
                @Override
                public void onComplete() {
                    System.out.println();
                    System.out.flush();
                }
            });

            try {
                future.get();
            } catch (ExecutionException ignore) {
                // ignore
            }
        }
    }

}
