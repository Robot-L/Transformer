package com.robot.transform;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * 转换器配置类
 * 约定自定义转换器都放在api包的transformer目录下，方便给其他模块使用
 *
 * @author R
 * @since 2022-10-5
 */
@Configuration
@ComponentScan("com.**.transformer")
public class TransformAutoConfiguration {

}
