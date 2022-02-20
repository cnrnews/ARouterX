package com.wangyi.compiler.utils;

import java.util.Collection;
import java.util.Map;

/**
 * @Author lihl
 * @Date 2022/2/19 15:38
 * @Email 1601796593@qq.com
 */
public class EmptyUtils {

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
