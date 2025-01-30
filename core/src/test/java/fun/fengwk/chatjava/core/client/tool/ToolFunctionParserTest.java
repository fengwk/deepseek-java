package fun.fengwk.chatjava.core.client.tool;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author fengwk
 */
public class ToolFunctionParserTest {

    @Test
    public void test() {
        ToolFunctionsDemo toolFunctionsDemo = new ToolFunctionsDemo();
        ToolFunctionParser toolFunctionParser = new ToolFunctionParser();
        List<ToolFunctionHandler> handlers = toolFunctionParser.parse(toolFunctionsDemo);
        assertFalse(handlers.isEmpty());

        ToolFunctionHandler handler = handlers.get(0);
        assertEquals(handler.getDescription(), ToolFunctionsDemo.DESC);
        assertEquals(handler.getName(), "ToolFunctionsDemo_getWeather");

        String ret = handler.call("{\"year\": \"2024\", \"month\": \"10\", \"day\": \"13\"}");
        assertEquals(new ToolFunctionsDemo().getWeather("2024", "10", "13"), ret);
    }

}
