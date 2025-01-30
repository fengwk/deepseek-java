package fun.fengwk.chatjava.core.client.util.json;

import java.lang.reflect.Type;
import java.util.ServiceLoader;

/**
 * @author fengwk
 */
public class ChatJsonUtils {

    private static volatile ChatJsonAdapter INSTANCE;

    private ChatJsonUtils() {}

    static {
        autoRegister();
    }

    private static void autoRegister() {
        ServiceLoader<ChatJsonAdapterProvider> sl = ServiceLoader.load(ChatJsonAdapterProvider.class);
        for (ChatJsonAdapterProvider provider : sl) {
            ChatJsonAdapter chatJsonAdapter = provider.getChatJsonAdapter();
            if (chatJsonAdapter != null) {
                register(chatJsonAdapter);
                return;
            }
        }

        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            register(new JacksonChatJsonAdapter());
            return;
        } catch (ClassNotFoundException ignore) {
            // nothing to do
        }

        try {
            Class.forName("com.google.gson.Gson");
            register(new GsonJsonChatJsonAdapter());
            return;
        } catch (ClassNotFoundException ignore) {
            // nothing to do
        }

        try {
            Class.forName("com.alibaba.fastjson2.JSON");
            register(new FastJsonChatJsonAdapter());
            return;
        } catch (ClassNotFoundException ignore) {
            // nothing to do
        }

        throw new ExceptionInInitializerError("can not found json dependency");
    }

    public static void register(ChatJsonAdapter jsonAdapter) {
        INSTANCE = jsonAdapter;
    }

    public static String toJson(Object obj) {
        return INSTANCE.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return INSTANCE.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type type) {
        return INSTANCE.fromJson(json, type);
    }

}
