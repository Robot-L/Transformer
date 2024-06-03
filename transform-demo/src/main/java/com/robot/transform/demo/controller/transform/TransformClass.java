package com.robot.transform.demo.controller.transform;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.robot.transform.annotation.Transform;
import com.robot.transform.serialize.TransformSerializer;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 班级转换（自定义转换注解）
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
@Transform(transformer = ClassTransformer.class)
@JsonSerialize(using = TransformSerializer.class)
@JacksonAnnotationsInside
public @interface TransformClass {

    /**
     * 目标字段
     * <p>
     * 默认自动推断（推断规则：如注解标注的字段是sex，自动推断结果为“sexName”，“sexId”或“sexCode”）
     */
    @AliasFor(annotation = Transform.class)
    String value() default "";
}
