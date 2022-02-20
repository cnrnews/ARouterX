package com.wangyi.api.core;

import java.util.Map;

/**
 * @Author lihl
 * @Date 2022/2/18 14:35
 * @Email 1601796593@qq.com
 *
 * 路由组 Group 对外提供加载数据接口
 */
public interface ARouterLoadGroup {

    /**
     * 加载路由组数据
     * 比如： app, ARouter$$Path$$app.class (实现了 ARouterLadPath 接口)
     * @return key: app , value: "app" 分组对应的路由详情
     */
    Map<String, Class<? extends ARouterLoadPath>> loadGroup();
}
