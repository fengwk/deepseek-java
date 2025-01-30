package fun.fengwk.chatjava.core.client.util.json;

import com.alibaba.fastjson2.JSON;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class FastJsonChatJsonAdapter implements ChatJsonAdapter {

    @Override
    public String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        return JSON.parseObject(json, typeOfT);
    }

}
