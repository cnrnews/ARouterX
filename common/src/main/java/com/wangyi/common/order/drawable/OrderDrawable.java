package com.wangyi.common.order.drawable;

import com.wangyi.api.core.Call;

/**
 * @Author lihl
 * @Date 2022/2/20 9:40
 * @Email 1601796593@qq.com
 * 订单模块对外暴露接口实现类，其他模块可以获取返回 res 资源
 */
public interface OrderDrawable extends Call {
    int getDrawable();
}
