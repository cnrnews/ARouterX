package com.wangyi.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LruCache;

import com.wangyi.annotation.model.RouterBean;
import com.wangyi.api.core.ARouterLoadGroup;
import com.wangyi.api.core.ARouterLoadPath;

/**
 * @Author lihl
 * @Date 2022/2/20 9:44
 * @Email 1601796593@qq.com
 */
public class RouterManager {

    // 路由组名
    private String group;

    // 路由 path 路径
    private String path;

    //Lru 缓存，key: 组名，value：路由组Group加载接口
    private LruCache<String, ARouterLoadGroup> groupLruCache;
    //Lru 缓存，key: 类路径，value：路由Path路径加载接口
    private LruCache<String, ARouterLoadPath> pathLruCache;

    //APT 生成类文件后缀名
    private static final String GROUP_FILE_PREFIX_NAME = ".ARouter$$Group$$";

    private static RouterManager instance;

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    private RouterManager() {
        groupLruCache = new LruCache<>(163);
        pathLruCache = new LruCache<>(163);
    }

    /**
     * 传递路由地址
     *
     * @param path
     * @return
     */
    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("未按照规范配置，如：/app/MainActivity");
        }
        group = subFromPath2Group(path);

        // 检查过了 path 和 group
        this.path = path;

        return new BundleManager();
    }

    private String subFromPath2Group(String path) {

        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("未按照规范配置，如：/app/MainActivity");
        }

        //
        String finalGroup = path.substring(1, path.indexOf("/", 1));

        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("未按照规范配置，如：/app/MainActivity");
        }

        return finalGroup;
    }

    /**
     * 完成页面跳转
     *
     * @param context
     * @param bundleManager
     * @param code
     * @return
     */
    public RouterManager navigation(Context context, BundleManager bundleManager, int code) {

        // ARouter$$Group$$order
        String groupClassName = context.getPackageName() + ".apt." + GROUP_FILE_PREFIX_NAME + group;

        try {
            ARouterLoadGroup groupLoad = groupLruCache.get(group);
            if (groupLoad == null) {
                groupLoad = (ARouterLoadGroup) Class.forName(groupClassName).newInstance();
                groupLruCache.put(group, groupLoad);
            }

            // 判断
            if (groupLoad.loadGroup().isEmpty()) {
                throw new RuntimeException("路由表加载失败！");
            }

            // 读取路由 Path路径类文件缓存
            ARouterLoadPath pathLoad = pathLruCache.get(path);
            if (pathLoad == null) {

                // 通过组Group加载接口，获取Path加载接口
                Class<? extends ARouterLoadPath> clazz = groupLoad.loadGroup().get(group);
                if (clazz != null) {
                    pathLoad = clazz.newInstance();
                }
                if (pathLoad != null) {
                    pathLruCache.put(path, pathLoad);
                }
            }

            if (pathLoad != null) {
                if (pathLoad.loadPath().isEmpty()) {
                    throw new RuntimeException("路由表Path加载失败!");
                }
                RouterBean routerBean = pathLoad.loadPath().get(path);
                if (routerBean != null) {
                    switch (routerBean.getType()) {
                        case ACTIVITY:
                            Intent intent = new Intent(context, routerBean.getClazz());
                            intent.putExtras(bundleManager.getBundle());

                            //
                            if (bundleManager.isResult()) {
                                ((Activity) context).setResult(code,intent);
                                ((Activity) context).finish();
                            }

                            if (code > 0) {
                                ((Activity) context).startActivityForResult(intent, code, bundleManager.getBundle());
                            } else {
                                context.startActivity(intent, bundleManager.getBundle());
                            }
                            break;
                        case CALL:
                            return (RouterManager) routerBean.getClazz().newInstance();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public RouterManager withBundle(Bundle bundle) {
        return this;
    }
}
