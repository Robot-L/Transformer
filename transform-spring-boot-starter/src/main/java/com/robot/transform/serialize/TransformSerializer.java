package com.robot.transform.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.robot.dict.Dict;
import com.robot.transform.annotation.Transform;
import com.robot.transform.transformer.Transformer;
import com.robot.transform.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * <p>
 * 序列化
 * </p>
 *
 * @author miaoyj
 * @since 2022-09-13
 */
public class TransformSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    public TransformSerializer() {
    }

    public TransformSerializer(Annotation transformAnnotation, Class<? extends Transformer> transformer) {
        this.transformAnnotation = transformAnnotation;
        this.transformer = transformer;
    }

    private Annotation transformAnnotation;
    private Class<? extends Transformer> transformer;

    @Override
    public void serialize(Object obj, JsonGenerator gen, SerializerProvider provider) {
        try {
            Object originValue = obj;
            Object value = null;
            //枚举特殊处理
            if (obj instanceof Dict) {
                Dict dict = (Dict) obj;
                originValue = dict.getCode();
            }
            //输出原始值
            gen.writeObject(originValue);

            Transformer transformerObj = SpringContextUtil.getBean(transformer);
            if (transformerObj != null) {
                //调用转换器
                value = transformerObj.transform(obj, this.transformAnnotation);
            }
            String filedName = gen.getOutputContext().getCurrentName();
            Object outObj = gen.getOutputContext().getCurrentValue();
            String targetFieldName = getTargetFieldName();
            //设置转换后的值到目标字段
            setTargetFiledValue(value, filedName, outObj, targetFieldName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * 设置转换后的值到目标字段
     * </p>
     *
     * @param targetFiledValue 值
     * @param sourceFiledName  源字段
     * @param outObj
     * @param targetFieldName  目标字段
     */
    private static void setTargetFiledValue(Object targetFiledValue, String sourceFiledName, Object outObj, String targetFieldName) throws IllegalAccessException, InvocationTargetException {
        if (StringUtils.isNotBlank(targetFieldName)) {
            targetFieldName = targetFieldName.substring(0, 1).toUpperCase() + targetFieldName.substring(1);
        }
        String possibleName = String.format("set%s", targetFieldName);
        String possibleCode = possibleName;
        if (StringUtils.isBlank(targetFieldName)) {
            possibleName = String.format("set%sName", sourceFiledName);
            possibleCode = String.format("set%sCode", possibleCode);
        }
        Class cls = outObj.getClass();
        String finalPossibleName = possibleName;
        String finalPossibleCode = possibleCode;
        Method setMethod = Arrays.stream(cls.getMethods()).filter(x ->
                StringUtils.equalsAnyIgnoreCase(x.getName(), finalPossibleName, finalPossibleCode)).findFirst().orElse(null);
        ;
        if (setMethod != null) {
            setMethod.invoke(outObj, targetFiledValue);
        }
    }

    /**
     * <p>
     * 获取目标字段名
     * </p>
     *
     * @return /
     */
    private String getTargetFieldName() throws IllegalAccessException, InvocationTargetException {
        String targetFieldName = null;
        //获取注解内的value字段
        Method valueMethod = Arrays.stream(this.transformAnnotation.getClass().getMethods()).filter(x ->
                StringUtils.equalsAnyIgnoreCase(x.getName(), "value")).findFirst().orElse(null);
        if (valueMethod != null) {
            targetFieldName = valueMethod.invoke(this.transformAnnotation).toString();
        }
        return targetFieldName;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty beanProperty) {
        Annotation annotation = null;
        Iterable<Annotation> annotations = beanProperty.getMember().getAllAnnotations().annotations();
        for (Annotation a : annotations) {
            //例：查找TransformEnum 是有@Transform(transformer = EnumTransformer.class)
            Annotation transform1 = AnnotationUtils.getAnnotation(a.annotationType(), Transform.class);
            if (transform1 != null) {
                annotation = a;
                break;
            }
        }
        Transform transformAnnotation = beanProperty.getAnnotation(Transform.class);
        if (annotation != null) {
            return new TransformSerializer(annotation, transformAnnotation.transformer());
        }
        return this;
    }
}
