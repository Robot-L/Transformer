package com.robot.transform.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.robot.dict.spring.jackson.DictModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * <p>
 * jackson转换消息
 * </p>
 *
 * @author miaoyj
 * @version 1.0.0-SNAPSHOT
 * @since 2020-07-09
 */
public class JacksonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    /**
     * <p>
     * 构造函数
     * </p>
     *
     * @author miaoyj
     * @since 2020-09-09
     */
    public JacksonHttpMessageConverter() {
        ObjectMapper objectMapper = getObjectMapper();

        SimpleModule module = new SimpleModule("TransformModule");
        module.setSerializerModifier(new TransformSerialization.UpperCaseSerializerModifier());

        objectMapper.registerModule(module);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        //枚举
        objectMapper.registerModule(new DictModule());
    }
}
