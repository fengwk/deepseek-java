package fun.fengwk.chatjava.core.client;

import fun.fengwk.chatjava.core.client.request.ChatRequest;
import fun.fengwk.chatjava.core.client.response.ChatResponse;
import fun.fengwk.chatjava.core.client.util.ChatUtils;
import fun.fengwk.chatjava.core.client.util.httpclient.ChatHttpClientFactory;
import fun.fengwk.chatjava.core.client.util.httpclient.ChatHttpClientUtils;
import fun.fengwk.chatjava.core.client.util.json.ChatJsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Chat GPT 客户端
 *
 * @author fengwk
 */
@Slf4j
public class HttpClientChatClient implements ChatClient {

    private volatile ChatClientOptions clientOptions;
    private final HttpClient httpClient;

    public HttpClientChatClient(ChatClientOptions chatClientOptions) {
        this(chatClientOptions, ChatHttpClientFactory.getDefaultHttpClient());
    }

    public HttpClientChatClient(ChatClientOptions clientOptions, HttpClient httpClient) {
        this.clientOptions = clientOptions;
        this.httpClient = httpClient;
    }

    @Override
    public ChatClientOptions getClientOptions() {
        return clientOptions;
    }

    @Override
    public void setClientOptions(ChatClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

    @Override
    public ChatCompletionsResponse chatCompletions(ChatRequest chatRequest, ChatClientOptions clientOptions) {
        HttpRequest httpRequest = buildChatCompletionHttpRequest(chatRequest, clientOptions);

        String errorMessage;
        Throwable error;
        try {
            log.debug("chatCompletions request, url: {}, chatRequest: {}",
                clientOptions.getChatCompletionsUrl(), ChatJsonUtils.toJson(chatRequest));
            HttpResponse<InputStream> httpResponse = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            String respBody = ChatHttpClientUtils.parseBodyString(httpResponse);
            if (ChatHttpClientUtils.success(httpResponse.statusCode())) {
                log.debug("chatCompletions response success, respBody: {}", respBody);
                try {
                    ChatResponse chatResponse = ChatJsonUtils.fromJson(respBody, ChatResponse.class);
                    return new ChatCompletionsResponse(true, chatResponse, null, null);
                } catch (Exception ex) {
                    errorMessage = respBody;
                    error = ex;
                }
            } else {
                log.error("ChatClient.chatCompletions failed, status: {}, body: {}",
                    httpResponse.statusCode(), respBody);
                errorMessage = respBody;
                error = new IllegalStateException("chatCompletions http status: " + httpResponse.statusCode());
            }
        } catch (IOException ex) {
            errorMessage = ex.getMessage();
            error = ex;
            log.error("ChatClient.chatCompletions failed", ex);
        } catch (InterruptedException ex) {
            errorMessage = ex.getMessage();
            error = ex;
            log.warn("ChatClient.chatCompletions interrupted");
            Thread.currentThread().interrupt();
        }

        return new ChatCompletionsResponse(false, null, errorMessage, error);
    }

    @Override
    public CompletableFuture<ChatCompletionsResponse> streamChatCompletions(
        ChatRequest chatRequest, StreamChatListener chatListener, ChatClientOptions clientOptions) {
        chatRequest.setStream(true);
        ChatUtils.streamIncludeUsage(chatRequest);
        HttpRequest httpRequest = buildChatCompletionHttpRequest(chatRequest, clientOptions);
        log.debug("streamChatCompletions request, url: {}, chatRequest: {}",
            clientOptions.getChatCompletionsUrl(), ChatJsonUtils.toJson(chatRequest));
        CompleteResponseStreamChatListener completeChatListener = new CompleteResponseStreamChatListenerDecorator(chatListener);
        return httpClient.sendAsync(httpRequest,
            HttpResponse.BodyHandlers.fromLineSubscriber(
                new StreamChatListenerAdapter(completeChatListener)))
            .thenApply(resp -> completeChatListener.toChatCompletionsResponse());
    }

    private HttpRequest buildChatCompletionHttpRequest(ChatRequest chatRequest, ChatClientOptions clientOptions) {
        // body
        String reqBody = ChatJsonUtils.toJson(chatRequest);
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(reqBody, StandardCharsets.UTF_8);
        // request
        return HttpRequest.newBuilder()
            .uri(clientOptions.getChatCompletionsUrl())
            .POST(bodyPublisher)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + clientOptions.getToken())
            .timeout(clientOptions.getPerHttpRequestTimeout())
            .build();
    }

}
