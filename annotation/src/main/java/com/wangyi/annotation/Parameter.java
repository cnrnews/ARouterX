package com.wangyi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author lihl
 * @Date 2022/2/20 7:24
 * @Email 1601796593@qq.com
 *
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Parameter {

    // 不填写name的注解值表示属性名就是key,填写了就用注解值作为key
    // 从 getIntent 方法中获取传递参数值
    String name() default "";
}
