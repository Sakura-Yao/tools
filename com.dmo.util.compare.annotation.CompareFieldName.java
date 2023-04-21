package com.dmo.uip.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义对比的字段名称
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompareFieldName {

    String fieldName();
}
