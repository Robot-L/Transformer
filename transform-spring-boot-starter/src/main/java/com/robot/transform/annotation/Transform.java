package com.robot.transform.annotation;

import com.robot.transform.transformer.Transformer;

import java.lang.annotation.*;

/**
 * 转换注解
 * 最基本的注解，可以被其他自定义注解衍生
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Transform {

    /**
     * 指定转换器
     */
    Class<? extends Transformer> transformer() default Transformer.class;

    /**
     * 目标字段
     * <p>
     * 默认自动推断（推断规则：如注解标注的字段是sex，自动推断结果为“sexName”，“sexId”或“sexCode”）
     */
    String value() default "";

    /**
     * 异步转换功能（即将实现，敬请期待）
     */
    boolean async() default false;

    int cacheTime() default -1;
}
