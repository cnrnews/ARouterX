package com.wangyi.api.core;

/**
 * @Author lihl
 * @Date 2022/2/20 7:21
 * @Email 1601796593@qq.com
 * 参数 Parameter 加载接口
 */
public interface ParameterLoad {
    /**
     * 目标对象，属性名 = target.getIntent(),属性类型(注解值 or 属性名); 完成赋值
     * @param object 目标对象 如 MainActivity
     */
    void loadParameter(Object object);
}
