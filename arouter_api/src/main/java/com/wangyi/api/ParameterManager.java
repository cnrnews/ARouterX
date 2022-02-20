package com.wangyi.api;

import android.app.Activity;
import android.util.LruCache;

import com.wangyi.api.core.ParameterLoad;

/**
 * @Author lihl
 * @Date 2022/2/20 9:52
 * @Email 1601796593@qq.com
 */
public class ParameterManager {
    private static ParameterManager instance;

    // LRU 缓存，key: 类名,value : 参数 Parameter 加载接口
    private LruCache<String, ParameterLoad> cache;

    // APT 生成的获取参数类文件，后缀名
    private static final String FILE_SUFFIX_NAME = "$$Parameter";

    public static ParameterManager getInstance() {
        if (instance == null) {
            synchronized (ParameterManager.class) {
                if (instance == null) {
                    instance = new ParameterManager();
                }
            }
        }
        return instance;
    }

    private ParameterManager() {
        cache = new LruCache<>(163);
    }

    public void loadParameter(Activity activity) {
        String className = activity.getClass().getName();
        ParameterLoad parameterLoad = cache.get(className);
        try {
            if (parameterLoad == null) {
                parameterLoad = (ParameterLoad) Class.forName(className+FILE_SUFFIX_NAME).newInstance();
                cache.put(className, parameterLoad);
            }
            parameterLoad.loadParameter(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
