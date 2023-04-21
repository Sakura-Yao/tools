package com.dmo.uip.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 对不需要进行比对的字段添加这个注解
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompareIgnore {

}
