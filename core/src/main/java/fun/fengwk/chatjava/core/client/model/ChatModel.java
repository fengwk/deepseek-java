package fun.fengwk.chatjava.core.client.model;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ChatModel {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 描述
     */
    private String description;

    /**
     * 上下文长度
     */
    private int contextWindow;

    /**
     * 最大输出长度
     */
    private int maxTokens;

    /**
     * 输入价格描述
     */
    private String inputPriceDescription;

    /**
     * 输出价格描述
     */
    private String outputPriceDescription;


}
