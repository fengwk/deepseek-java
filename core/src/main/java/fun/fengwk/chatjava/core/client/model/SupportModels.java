package fun.fengwk.chatjava.core.client.model;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 支持的模型表
 *
 * @author fengwk
 */
@Deprecated
public class SupportModels {

    private static final ConcurrentMap<String, ChatModel> MODELS = new ConcurrentHashMap<>();

    static {
        // https://platform.openai.com/docs/models/gpt-4o
        ChatModel gtp4o = new ChatModel();
        gtp4o.setModel("gpt-4o");
        gtp4o.setDescription("GPT-4o: Our high-intelligence flagship model for complex, multi-step tasks. " +
            "GPT-4o is cheaper and faster than GPT-4 Turbo. Currently points to gpt-4o-2024-08-06.");
        gtp4o.setContextWindow(128000);
        gtp4o.setMaxTokens(16384);
        putModel(gtp4o);

        // https://api-docs.deepseek.com/zh-cn/quick_start/pricing
        ChatModel deepseek = new ChatModel();
        deepseek.setModel("deepseek-chat");
        deepseek.setDescription("deepseek chat");
        deepseek.setContextWindow(128000);
        deepseek.setMaxTokens(4000);
        deepseek.setInputPriceDescription("1元/百万tokens");
        deepseek.setOutputPriceDescription("2元/百万tokens");
        putModel(deepseek);
    }

    private static void putModel(ChatModel chatModel) {
        MODELS.put(chatModel.getModel(), chatModel);
    }

    /**
     * 获取支持的所有模型名称
     *
     * @return 支持的所有模型名称
     */
    public static Set<String> getAllModels() {
        return Collections.unmodifiableSet(MODELS.keySet());
    }

    /**
     * 检查是否支持指定模型
     *
     * @param model 模型名称
     * @return 是否支持
     */
    public static boolean supportModel(String model) {
        return MODELS.containsKey(model);
    }

    /**
     * 获取模型元数据
     *
     * @param model 模型名称
     * @return ChatModel
     */
    public static ChatModel getModel(String model) {
        return MODELS.get(model);
    }

    /**
     * 获取模型元数据
     *
     * @param model 模型名称
     * @return ChatModel
     */
    public static ChatModel getModeRequired(String model) {
        ChatModel chatModel = getModel(model);
        if (chatModel == null) {
            throw new IllegalArgumentException("not support model: " + model);
        }
        return chatModel;
    }

}
