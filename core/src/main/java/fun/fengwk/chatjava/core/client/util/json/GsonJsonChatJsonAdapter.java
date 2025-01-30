package fun.fengwk.chatjava.core.client.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class GsonJsonChatJsonAdapter implements ChatJsonAdapter {

    private final Gson gson;

    public GsonJsonChatJsonAdapter() {
        this(buildDefault());
    }

    public GsonJsonChatJsonAdapter(Gson gson) {
        this.gson = gson;
    }

    private static Gson buildDefault() {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();// 关闭html转义
        return builder.create();
    }

    @Override
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

}
