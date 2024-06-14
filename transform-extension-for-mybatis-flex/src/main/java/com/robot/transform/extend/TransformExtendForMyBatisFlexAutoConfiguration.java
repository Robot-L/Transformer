package com.robot.transform.extend;

import com.robot.transform.extend.transformer.ForeignKeyTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 转换器配置类
 *
 * @author R
 */
@Configuration
public class TransformExtendForMyBatisFlexAutoConfiguration {

    /**
     * 注册外键转换器
     */
    @Bean
    public ForeignKeyTransformer foreignKeyTransformer() {
        return new ForeignKeyTransformer();
    }

}
