package fun.fengwk.chatjava.core.client.util.json;

import java.lang.reflect.Type;

/**
 * 提供一个JSON处理适配器接口用于兼容不同的JSON库
 *
 * @author fengwk
 */
public interface ChatJsonAdapter {

    String toJson(Object obj);

    <T> T fromJson(String json, Class<T> classOfT);

    <T> T fromJson(String json, Type typeOfT);

}
