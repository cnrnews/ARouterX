package com.wangyi.order.impl;

import com.wangyi.annotation.ARouter;
import com.wangyi.common.order.drawable.OrderDrawable;
import com.wangyi.order.R;

/**
 * @Author lihl
 * @Date 2022/2/20 9:39
 * @Email 1601796593@qq.com
 *
 * 订单模块对外暴露接口实现类，其他模块可以获取返回 res 资源
 */
@ARouter(path = "/order/getDrawable")
public class OrderDrawableImpl implements OrderDrawable {
    @Override
    public int getDrawable() {
        return R.drawable.ic_launcher_background;
    }
}
