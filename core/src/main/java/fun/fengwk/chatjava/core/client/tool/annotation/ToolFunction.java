package fun.fengwk.chatjava.core.client.tool.annotation;

import java.lang.annotation.*;

/**
 * 工具函数
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ToolFunction {

    /**
     * 获取函数描述
     *
     * @return 函数描述
     */
    String description() default "";

}
