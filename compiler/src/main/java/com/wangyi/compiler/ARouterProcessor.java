package com.wangyi.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.wangyi.annotation.ARouter;
import com.wangyi.annotation.model.RouterBean;
import com.wangyi.compiler.utils.Constants;
import com.wangyi.compiler.utils.EmptyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 注解处理器
 */
@AutoService(Processor.class)
public class ARouterProcessor extends AbstractProcessor {

    private static final String TAG = "ARouterProcessor";
    // 操作 Element 工具类
    private Elements elementUtils;

    // type (类信息) 工具类
    private Types typeUtils;

    // 用来输出警告、错误等日志
    private Messager messager;

    // 文件生成器
    private Filer filter;

    private String moduleName;
    private String packageNameForAPT;

    // 存放路由组对应的详细Path类对象
    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();
    private Map<String, String> tempGroupMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filter = processingEnvironment.getFiler();

        // 获取传递的参数
        Map<String, String> options =
                processingEnvironment.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForAPT = options.get(Constants.APT_PACKAGE);

            messager.printMessage(Diagnostic.Kind.NOTE, "moduleName >>> " + moduleName);
            messager.printMessage(Diagnostic.Kind.NOTE, "packageNameForAPT >>> " + packageNameForAPT);
        }

        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的参数 moduleName、packageNameForAPT 为空，请在对应的 build.gradle 配置参数");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (!EmptyUtils.isEmpty(set)) {
            // 获取所有添加了注解的类
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
            if (!EmptyUtils.isEmpty(elements)) {
                try {
                    parseElements(elements);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }


//
//
//        System.out.println(TAG + " >>> all Destination elements count = " + elements.size());
//
//        // 当未搜集到 @Destination 注解的时候，跳过后续流程
//        if (elements.size() < 1) {
//            return false;
//        }
//
//        // 遍历所有注解类信息
//        for (Element element : elements) {
//            // 包名
//            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
//            // 类名
//            String className = element.getSimpleName().toString();
//            messager.printMessage(Diagnostic.Kind.NOTE, "被注解的类名：" + className);
//
//
//            String finalClassName = className + "$$ARouter";
//
//            System.out.println("finalClassName>>" + finalClassName);
//
//            ARouter router = element.getAnnotation(ARouter.class);
//
//            // 生成方法
//            MethodSpec methodSpec = MethodSpec.methodBuilder("findTargetClass")
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .returns(Class.class)
//                    .addParameter(String.class, "path")
//                    .addStatement("return path.equals($S) ? $T.class : null", router.path(), ClassName.get((TypeElement) element))
//                    .build();
//            // 生成类
//            TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
//                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                    .addMethod(methodSpec)
//                    .build();
//
//            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
//                    .build();
//
//            try {
//                // 写入文件
//                javaFile.writeTo(filter);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
        return true;
    }

    /**
     * 生成路由组 Group 对应的详细 Path，如： ARouter$Path$app
     *
     * @param pathLoadType ARouterPath 接口信息
     */
    private void createPathFile(TypeElement pathLoadType) throws IOException {
        if (EmptyUtils.isEmpty(tempPathMap)) return;

        // 方法返回值 Map<String,RouterBean>
        ParameterizedTypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class)
        );

        // 遍历分组，每一个分组创建一个路径类文件，如: ARouter$$Path$$app
        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {
            // 方法体构造函数 public Map<String,RouterBean> loadPath()
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.PATH_METHOD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturns);

            // 不循环部分 Map<String,RouterBean> pathMap = new HashMap()
            methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETER_NAME,
                    ClassName.get(HashMap.class));

            // /app/MainActivity ...
            List<RouterBean> pathList = entry.getValue();
            for (RouterBean bean : pathList) {
                // 方法内容的循环部分
                //          pathMap.put("/app/MainActivity",
                //                    RouterBean.create(RouterBean.Type.ACTIVITY,
                //                            MainActivity.class,
                //                            "/app/MainActivity",
                //                            "app"
                //                            ));
                methodBuilder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        Constants.PATH_PARAMETER_NAME,
                        bean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        bean.getType(),
                        ClassName.get((TypeElement) bean.getElement()),
                        bean.getPath(),
                        bean.getGroup());
            }

            // 遍历过后，最后 return pathMap
            methodBuilder.addStatement("return $N", Constants.PATH_PARAMETER_NAME);

            // 生成类文件，如 ARouter$$Path$$app
            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由path类文件为:" + packageNameForAPT + "." + finalClassName);

            JavaFile.builder(packageNameForAPT,
                    TypeSpec.classBuilder(finalClassName)
                            .addSuperinterface(ClassName.get(pathLoadType)) // 实现接口
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build()) // 方法构建
                            .build()
            ).build()
                    .writeTo(filter);


            // 添加路由组 map
            tempGroupMap.put(entry.getKey(), finalClassName);
        }
    }

    /**
     * 生成路由组Group文件， 如 ARouter$$Group$$app
     *
     * @param groupLoadType ARouterLoadGroup 接口信息
     * @param pathLoadType  ARouterLoadPath 接口信息
     */
    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {
        // 判断是否有需要生成的文件
        if (EmptyUtils.isEmpty(tempGroupMap) || EmptyUtils.isEmpty(tempPathMap)) return;

        ParameterizedTypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                // 是否是 ARouterLoadPath 的子类
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType)))
        );

        // 方法配置 public Map<String ,Class<? extends ARouterLoadPath>> loadGroup()
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.GROUP_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturns);

        // 遍历之前: Map<String ,Class<? extends ARouterLoadPath>> groupMap = new HashMap()
        methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                Constants.GROUP_PARAMETER_NAME,
                ClassName.get(HashMap.class));

        // 方法内容配置
        for (Map.Entry<String, String> entry : tempGroupMap.entrySet()) {
            // groupMap.put("main",ARouter$$Path$$app.class)
            methodBuilder.addStatement("$N.put($S,$T.class)",
                    Constants.GROUP_PARAMETER_NAME,
                    entry.getKey(),
                    // 类文件在指定包名下
                    ClassName.get(packageNameForAPT, entry.getValue()));
        }

        // 便利之后，return groupMap
        methodBuilder.addStatement("return $N", Constants.GROUP_PARAMETER_NAME);

        // 最终生成的类文件
        String finalClassName = Constants.GROUP_FILE_NAME + moduleName;
        messager.printMessage(Diagnostic.Kind.NOTE, "APT 生成路由组Group 类文件: " + packageNameForAPT + "." + finalClassName);

        // 生成类文件 ARouter$$Group$$app
        JavaFile.builder(packageNameForAPT,
                TypeSpec.classBuilder(finalClassName)
                        .addSuperinterface(ClassName.get(groupLoadType))  // 实现接口
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(methodBuilder.build())
                        .build()).build().writeTo(filter);
    }

    /**
     * 解析所有添加了 @ARouter 注解的集合
     *
     * @param elements
     */
    private void parseElements(Set<? extends Element> elements) throws IOException {

        // 获取 Activity 类型
        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);
        TypeElement callType = elementUtils.getTypeElement(Constants.CALL);

        // 显示类信息
        TypeMirror activityMirror = activityType.asType();
        TypeMirror callMirror = callType.asType();


        for (Element element : elements) {
            // 获取每个元素的类信息
            TypeMirror elementMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.NOTE, "遍历的元素信息为：" + elementMirror.toString());

            ARouter aRouter = element.getAnnotation(ARouter.class);

            // 路由详细信息，封装到实体类
            RouterBean routerBean = new RouterBean.Builder()
                    .setGroup(aRouter.group())
                    .setPath(aRouter.path())
                    .setElement(element)
                    .build();

            // @ARouter 注解只能应用在类上，并且是规定的 Activity
            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                routerBean.setType(RouterBean.Type.ACTIVITY);
            } else if (typeUtils.isSubtype(elementMirror, callMirror)) {
                routerBean.setType(RouterBean.Type.CALL);
            }  else {
                throw new RuntimeException("目前 @ARouter 注解只能作用在 Activity 上");
            }

            valueOfPathMap(routerBean);
        }

        //
        TypeElement groupLoadType = elementUtils.getTypeElement(Constants.AROUTER_GROUP);
        TypeElement pathLoadType = elementUtils.getTypeElement(Constants.AROUTER_PATH);

        // 1. 生成路由的详细 Path 类文件，如: ARouter$$Path$$app
        createPathFile(pathLoadType);

        // 2.生成路由组 Group 类文件 ，如 ARouter$$Group$$app
        createGroupFile(groupLoadType, pathLoadType);
    }


    /**
     * 用来存储路由组对应的详细 Path 类对象
     *
     * @param routerBean
     */
    private void valueOfPathMap(RouterBean routerBean) {
        if (checkRouterPath(routerBean)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean >>> " + routerBean.toString());

            List<RouterBean> routerBeans = tempPathMap.get(routerBean.getGroup());
            if (EmptyUtils.isEmpty(routerBeans)) {
                routerBeans = new ArrayList<RouterBean>();
                routerBeans.add(routerBean);
                tempPathMap.put(routerBean.getGroup(), routerBeans);
            } else {
                routerBeans.add(routerBean);
            }
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，例如 :/app/MainActivity");
        }

    }

    /**
     * 校验 @ARouter 中注解的值，如果 group 未填写就从 path 中截取
     *
     * @param routerBean
     * @return
     */
    private boolean checkRouterPath(RouterBean routerBean) {

        String path = routerBean.getPath();
        String group = routerBean.getGroup();

        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如 /app/MainActivity");
            return false;
        }

        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如 /app/MainActivity");
            return false;
        }

        String finalGroup = path.substring(1, path.indexOf("/", 1));
        if (finalGroup.contains("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如 /app/MainActivity");
            return false;
        }

        if (!EmptyUtils.isEmpty(group) && !group.equals(moduleName)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的group值必须和当前子模块名相同");
            return false;
        } else {
            routerBean.setGroup(finalGroup);
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(ARouter.class.getCanonicalName());
        return annotations;
    }

    /**
     * 设置支持的参数
     *
     * @return
     */
    @Override
    public Set<String> getSupportedOptions() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Constants.MODULE_NAME);
        annotations.add(Constants.APT_PACKAGE);
        return annotations;
    }
}