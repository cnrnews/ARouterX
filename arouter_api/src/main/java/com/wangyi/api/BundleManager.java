package com.wangyi.api;

import android.content.Context;
import android.os.Bundle;

/**
 * @Author lihl
 * @Date 2022/2/20 10:14
 * @Email 1601796593@qq.com
 */
public class BundleManager {

    private Bundle bundle = new Bundle();

    // 是否回调 setResult();
    private boolean isResult;

    public Bundle getBundle() {
        return bundle;
    }

    public boolean isResult() {
        return isResult;
    }

    public BundleManager withString(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleManager withResultString(String key, String value) {
        bundle.putString(key, value);
        isResult = true;
        return this;
    }

    public BundleManager withBoolean(String key, Boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withInt(String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBundle(Bundle value) {
        this.bundle = bundle;
        return this;
    }

    public Object navigation(Context context){
        return navigation(context,-1);
    }

    /**
     * 回传 RouterManager ，进行页面跳转
     * @param context
     * @param code
     * @return
     */
    public Object navigation(Context context, int code) {
        return RouterManager.getInstance().navigation(context,this,code);
    }
}
