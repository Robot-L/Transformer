package com.robot.transform.annotation;


import com.robot.transform.transformer.EnumTransformer;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 枚举转换
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
@Transform(transformer = EnumTransformer.class)
public @interface TransformEnum {
    /**
     * 目标字段
     * <p>
     * 默认自动推断（推断规则：如注解标注的字段是sex，自动推断结果为“sexName”，“sexId”或“sexCode”）
     */
    @AliasFor(annotation = Transform.class)
    String value() default "";
}
