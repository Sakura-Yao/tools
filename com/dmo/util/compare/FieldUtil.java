package com.dmo.util.compare;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.dmo.uip.base.annotation.CompareFieldName;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * 字段比较工具类
 */
public class FieldUtil {

    // jsonKey -> 字段
    private static final String FIELD = "field";

    // jsonKey -> 字段名称
    private static final String FIELD_NAME = "fieldName";

    // jsonKey -> 原始值
    private static final String ORIGINAL_VALUE = "originalValue";

    // jsonKey -> 当前值
    private static final String CURRENT_VALUE = "currentValue";

    // 类字段比较器
    public static JSONArray compareField(@NotNull Object original, Object current) throws Exception {
        Class<?> clazz = original.getClass();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        // 字段比较
        compareFields(fieldList, original, current);
        // 生成
        return buildJsonResult(fieldList, original, current);
    }

    // 字段比较
    private static void compareFields(List<Field> fieldList, Object original, Object current) throws Exception {
        try {
            Iterator<Field> fieldIterator = fieldList.iterator();
            while (fieldIterator.hasNext()) {
                Field field = fieldIterator.next();
                field.setAccessible(true);
                Object v1 = field.get(original);
                Object v2 = field.get(current);
                if (isIgnore(field)) {
                    fieldIterator.remove();
                    continue;
                }
                if (v1 == null && v2 == null) {
                    fieldIterator.remove();
                    continue;
                }
                if (!(v1 != null && !v1.equals(v2))) {
                    fieldIterator.remove();
                }
            }
        } catch (Exception e) {
            throw new Exception("字段比较失败", e);
        }
    }

    // 判断是否需要忽略
    private static boolean isIgnore(@NotNull Field field) {
        AtomicBoolean ignore = new AtomicBoolean(false);
        Arrays.stream(field.getAnnotations()).findAny().ifPresent(annotation -> {
            if (annotation.annotationType().getSimpleName().equals("CompareIgnore")) {
                ignore.set(true);
            }
        });
        return ignore.get();
    }

    /**
     * 比较完成后 将比较结果封装成json
     * json的格式为 [{field: xxx, fieldName: xxx, originalValue: xxx, currentValue: xxx}]
     */
    private static JSONArray buildJsonResult(List<Field> fieldList, Object originalValue, Object currentValue)
        throws Exception {
        JSONArray jsonArray = new JSONArray();
        for (Field field : fieldList) {
            jsonArray.add(buildJsonResult(field, originalValue, currentValue));
        }
        return jsonArray;
    }

    // 封装每一项的比较结果
    private static JSONObject buildJsonResult(Field field, Object originalValue, Object currentValue) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(FIELD, field.getName());
            jsonObject.put(FIELD_NAME, getFieldName(field));
            jsonObject.put(ORIGINAL_VALUE, field.get(originalValue));
            jsonObject.put(CURRENT_VALUE, field.get(currentValue));
            return jsonObject;
        } catch (Exception e) {
            throw new Exception("数据封装时出现异常: " + e);
        }
    }

    // 获取自定义的字段名
    private static String getFieldName(Field field) {
        if (compareFieldNameIsExist(field)) {
            return Strings.isBlank(field.getAnnotation(CompareFieldName.class).fieldName()) ? field.getName()
                : field.getAnnotation(CompareFieldName.class).fieldName();
        } else {
            return field.getName();
        }
    }

    // 判断这个字段是否存在 CompareFieldName 注解
    private static boolean compareFieldNameIsExist(Field field) {
        AtomicBoolean ignore = new AtomicBoolean(false);
        Arrays.stream(field.getAnnotations()).findAny().ifPresent(annotation -> {
            if (annotation.annotationType().getSimpleName().equals("CompareFieldName")) {
                ignore.set(true);
            }
        });
        return ignore.get();
    }
}
