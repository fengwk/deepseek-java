package fun.fengwk.chatjava.core.agent;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author fengwk
 */
@Data
public class Agent {

    /**
     * Agent name
     */
    private String name;

    /**
     * Agent prompt
     */
    private String prompt;

    /**
     * Agent使用的模型
     */
    private String model;

    /**
     * Agent使用的模型参数表
     */
    private Map<String, Object> modelParameters;

    /**
     * 工具集
     */
    private Set<String> tools;

    /**
     * 上下文token数量限制
     */
    private Integer contextWindow;

    /**
     * 最大输出token数量限制
     */
    private Integer maxTokens;

}