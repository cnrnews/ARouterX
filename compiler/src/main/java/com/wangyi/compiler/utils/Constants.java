package com.wangyi.compiler.utils;

/**
 * @Author lihl
 * @Date 2022/2/19 14:43
 * @Email 1601796593@qq.com
 *
 * 常量配置类
 */
public class Constants {

    // 注解处理器支持的注解类型
    public static final String AROUTER_ANNOTATION_TYPES = "com.wangyi.annotation.ARouter";

    // 注解处理器支持的注解类型
    public static final String PARAMETER_ANNOTATION_TYPES = "com.wangyi.annotation.Parameter";


    // 每个模块对应的模块名
    public static final String MODULE_NAME = "moduleName";
    // 存放APT 生成的类文件
    public static final String APT_PACKAGE = "packageNameForAPT";


    // String 全类名
    public static final String STRING = "java.lang.String";

    // activity 全类名
    public static final String ACTIVITY = "android.app.Activity";


    // RouterManager 类名
    public static final String ROUTER_MANAGER = "RouterManager";

    // 包名前缀封装
    public static final String BASE_PACKAGE = "com.wangyi.api";
    // 路由组 Group 加载接口
    public static final String AROUTER_GROUP = BASE_PACKAGE + ".core.ARouterLoadGroup";
    // 路由组 Group 对应的详细 Path 路径
    public static final String AROUTER_PATH = BASE_PACKAGE + ".core.ARouterLoadPath";
    // 获取参数
    public static final String PARAMETER_LOAD = BASE_PACKAGE + ".core.ParameterLoad";
    // 跨模块业务，实现接口
    public static final String CALL = BASE_PACKAGE + ".core.Call";



    // 路由组 Group 对应的详细 Path 方法名
    public static final String PATH_METHOD_NAME = "loadPath";

    // 路由组 Group 方法名
    public static final String GROUP_METHOD_NAME = "loadGroup";

    // 加载参数方法名
    public static final String PARAMETER_METHOD_NAME = "loadParameter";

    // 路由组 Group 对应的详细 Path 参数名
    public static final String PATH_PARAMETER_NAME = "pathMap";

    // 路由组 Group  参数名
    public static final String GROUP_PARAMETER_NAME = "groupMap";

    // 获取参数，方法名
    public static final String PARAMETER_NAME = "target";

    // APT 生成的路由组 Group 对应的详细 Path 类文件名
    public static final String PATH_FILE_NAME = "ARouter$$Path$$";

    // APT 生成的路由组 Group  类文件名
    public static final String GROUP_FILE_NAME = "ARouter$$Group$$";

    // APT 生成的获取参数的类文件名
    public static final String PARAMETER_FILE_NAME = "$$Parameter";
}
