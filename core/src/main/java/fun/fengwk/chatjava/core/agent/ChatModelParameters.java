package fun.fengwk.chatjava.core.agent;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 模型参数
 *
 * @author fengwk
 */
@Data
public class ChatModelParameters {

    private static final String TEMPERATURE = "temperature";
    private static final String TOP_P = "topP";
    private static final String PRESENCE_PENALTY = "presencePenalty";
    private static final String FREQUENCY_PENALTY = "frequencyPenalty";

    /**
     * 温度
     */
    private Float temperature;

    /**
     * top p
     */
    private Float topP;

    /**
     * 重复惩罚
     */
    private Float presencePenalty;

    /**
     * 频率惩罚
     */
    private Float frequencyPenalty;

    /**
     * 从map视图转为ChatGptModelParameters
     *
     * @return ChatGptModelParameters
     */
    public static ChatModelParameters fromMap(Map<String, Object> map) {
        ChatModelParameters parameters = new ChatModelParameters();
        if (map != null) {
            parameters.setTemperature(toChatGptParameter(map.get(TEMPERATURE)));
            parameters.setTopP(toChatGptParameter(map.get(TOP_P)));
            parameters.setPresencePenalty(toChatGptParameter(map.get(PRESENCE_PENALTY)));
            parameters.setFrequencyPenalty(toChatGptParameter(map.get(FREQUENCY_PENALTY)));
        }
        return parameters;
    }

    private static Float toChatGptParameter(Object p) {
        if (p instanceof Number) {
            return ((Number) p).floatValue();
        }
        return null;
    }

    /**
     * 获取map视图
     *
     * @return map视图
     */
    public Map<String, Object> asMap() {
        Map<String, Object> parameterMap = new HashMap<>();
        if (getTemperature() != null) {
            parameterMap.put(TEMPERATURE, getTemperature());
        }
        if (getTopP() != null) {
            parameterMap.put(TOP_P, getTopP());
        }
        if (getPresencePenalty() != null) {
            parameterMap.put(PRESENCE_PENALTY, getPresencePenalty());
        }
        if (getFrequencyPenalty() != null) {
            parameterMap.put(FREQUENCY_PENALTY, getFrequencyPenalty());
        }
        return parameterMap;
    }

}