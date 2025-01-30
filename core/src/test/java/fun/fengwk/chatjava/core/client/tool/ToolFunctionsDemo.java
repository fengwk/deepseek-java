package fun.fengwk.chatjava.core.client.tool;

import fun.fengwk.chatjava.core.client.tool.annotation.ToolFunction;
import fun.fengwk.chatjava.core.client.tool.annotation.ToolFunctionParam;

/**
 * @author fengwk
 */
public class ToolFunctionsDemo {

    static final String DESC = "Get weather, if unknown return empty string";

    @ToolFunction(
        description = DESC
    )
    public String getWeather(
        @ToolFunctionParam(name = "year") String year,
        @ToolFunctionParam(name = "month") String month,
        @ToolFunctionParam(name = "day") String day) {
        return String.format("%s-%s-%s: 晴天 - 24摄氏度", year, month, day);
    }

}
