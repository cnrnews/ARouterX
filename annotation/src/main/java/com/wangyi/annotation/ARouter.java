package com.wangyi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS) // 编译时注解
@Target(ElementType.TYPE) // 只能作用在类上
public @interface ARouter {

    // 路由路径
    String path();

    // 路由组名
    String group() default "";
}