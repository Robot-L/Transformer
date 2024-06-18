package com.robot.transform.transformer;

import com.robot.dict.Dict;
import com.robot.transform.annotation.TransformEnum;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


/**
 * 枚举转换器
 *
 * @author R
 */
@Component
public class EnumTransformer implements Transformer<Dict, TransformEnum> {

    @Override
    @SuppressWarnings("unchecked")
    public String transform(@NonNull Dict dict, TransformEnum annotation) {
        return dict.getText();
    }
}
