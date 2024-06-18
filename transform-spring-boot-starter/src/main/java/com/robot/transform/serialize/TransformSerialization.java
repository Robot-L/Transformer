package com.robot.transform.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
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
import java.util.List;

/**
 * <p>
 * 自定义序列化
 * </p>
 *
 * @author miaoyj
 * @since 2024-06-14
 */
public class TransformSerialization {

    /***
     * <p>
     * 字段序列化
     * </p>
     *
     * @author miaoyj
     * @since 2024-06-14
     */
    public static class UpperCaseSerializerModifier extends BeanSerializerModifier {
        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            for (BeanPropertyWriter writer : beanProperties) {
                writer.assignSerializer(getTransformSerializer(writer));
            }
            return beanProperties;
        }

        private TransformSerializer getTransformSerializer(BeanPropertyWriter writer) {
            Annotation annotation = null;
            Transform transformAnnotation = null;
            Iterable<Annotation> annotations = writer.getMember().getAllAnnotations().annotations();
            for (Annotation a : annotations) {
                //例：查找TransformEnum 是有@Transform(transformer = EnumTransformer.class)
                transformAnnotation = AnnotationUtils.getAnnotation(a.annotationType(), Transform.class);
                if (transformAnnotation != null) {
                    annotation = a;
                    break;
                }
            }
            if (annotation != null && transformAnnotation != null) {
                return new TransformSerializer(annotation, transformAnnotation.transformer());
            }
            return null;
        }
    }

    /**
     * <p>
     * 序列化
     * </p>
     *
     * @author miaoyj
     * @since 2024-06-14
     */
    public static class TransformSerializer extends JsonSerializer<Object> {

        private Annotation transformAnnotation;
        private Class<? extends Transformer> transformer;

        public TransformSerializer() {
        }

        public TransformSerializer(Annotation transformAnnotation, Class<? extends Transformer> transformer) {
            this.transformAnnotation = transformAnnotation;
            this.transformer = transformer;
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
                possibleCode = String.format("set%sCode", sourceFiledName);
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

        @Override
        public void serialize(Object obj, JsonGenerator gen, SerializerProvider provider) {
            try {
                Object transValue = null;
                //输出原始值
                gen.writeObject(obj);

                Transformer transformerObj = SpringContextUtil.getBean(transformer);
                if (transformerObj != null) {
                    //调用转换器
                    transValue = transformerObj.transform(obj, this.transformAnnotation);
                }
                String filedName = gen.getOutputContext().getCurrentName();
                Object outObj = gen.getOutputContext().getCurrentValue();
                String targetFieldName = getTargetFieldName();
                //设置转换后的值到目标字段
                setTargetFiledValue(transValue, filedName, outObj, targetFieldName);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
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
    }
}
