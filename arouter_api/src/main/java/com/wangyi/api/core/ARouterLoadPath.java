package com.wangyi.api.core;

import com.wangyi.annotation.model.RouterBean;

import java.util.Map;

/**
 * @Author lihl
 * @Date 2022/2/18 14:36
 * @Email 1601796593@qq.com
 *
 * 路由组对应的详情 Path 加载数据接口
 * 比如:app 分组对应有哪些类需要加载
 */
public interface ARouterLoadPath {

    /**
     * 加载路由中的Path详细数据
     * 比如: app 分组下有哪些信息
     * @return key: /app/MainActivity, value: MainActivity 信息封装到 RouterBean 对象中
     */
    Map<String, RouterBean> loadPath();
}
