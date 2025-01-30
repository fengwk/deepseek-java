package fun.fengwk.chatjava.core.client.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
@Slf4j
public class JacksonChatJsonAdapter implements ChatJsonAdapter {

    private final ObjectMapper objectMapper;

    public JacksonChatJsonAdapter() {
        this(buildDefault());
    }

    public JacksonChatJsonAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static ObjectMapper buildDefault() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 反序列化时如果遇到未知属性则忽略而非报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时忽略null值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Override
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ex) {
            log.error("writeValueAsString error", ex);
            return null;
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, classOfT);
        } catch (Exception ex) {
            log.error("fromJson error", ex);
            return null;
        }
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReferenceWrapper<>(typeOfT));
        } catch (Exception ex) {
            log.error("fromJson error", ex);
            return null;
        }
    }

}
