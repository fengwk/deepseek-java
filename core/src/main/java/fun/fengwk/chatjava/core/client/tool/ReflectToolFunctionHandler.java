package fun.fengwk.chatjava.core.client.tool;

import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;
import fun.fengwk.chatjava.core.client.util.json.ChatJsonUtils;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author fengwk
 */
@Data
public class ReflectToolFunctionHandler implements ToolFunctionHandler {

    /**
     * 函数名称
     */
    private String name;

    /**
     * 函数描述
     */
    private String description;

    /**
     * 函数的参数
     */
    private JsonSchema parameters;

    /**
     * 执行函数的目标方法
     */
    private Method method;

    /**
     * 执行函数的目标对象
     */
    private Object target;

    @Override
    public String call(String arguments) {
        // 解析传入参数
        Map<String, Object> argumentMap = ChatJsonUtils.fromJson(arguments, Map.class);

        // 构建调用参数
        Map<String, JsonSchema> properties = parameters.getProperties();
        Object[] params = new Object[properties.size()];
        int idx = 0;
        for (Map.Entry<String, JsonSchema> entry : properties.entrySet()) {
            Object val = argumentMap.get(entry.getKey());
            String jsonVal = ChatJsonUtils.toJson(val);
            params[idx] = ChatJsonUtils.fromJson(jsonVal, entry.getValue().getJavaType());
            idx++;
        }

        // 执行目标方法
        Object ret = ChatMiscUtils.invokeMethod(method, target, params);
        return String.valueOf(ret);
    }

}
