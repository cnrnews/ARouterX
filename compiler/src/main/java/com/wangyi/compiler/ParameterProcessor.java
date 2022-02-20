package com.wangyi.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wangyi.annotation.Parameter;
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
 * 参数注解处理器
 */
@AutoService(Processor.class)
public class ParameterProcessor extends AbstractProcessor {

    // 操作 Element 工具类
    private Elements elementUtils;

    // type (类信息) 工具类
    private Types typeUtils;

    // 用来输出警告、错误等日志
    private Messager messager;

    // 文件生成器
    private Filer filter;

    private Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filter = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (!EmptyUtils.isEmpty(set)) {
            // 获取所有添加了注解的类
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Parameter.class);
            if (!EmptyUtils.isEmpty(elements)) {
                // 用临时的map存储，用于遍历生成代码
                valueOfParameterMap(elements);
                // 生成类文件
                try {
                    createParameterFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    private void createParameterFile() throws IOException {
        if (EmptyUtils.isEmpty(tempParameterMap)) return;

        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);
        // 获取 parameter 类型
        TypeElement parameterType = elementUtils.getTypeElement(Constants.PARAMETER_LOAD);

        // 参数配置
        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, Constants.PARAMETER_NAME).build();
        for (Map.Entry<TypeElement, List<Element>> entry : tempParameterMap.entrySet()) {

            // Map 集合中，key 是类名 如 MainActivity
            TypeElement typeElement = entry.getKey();
            // 如果类名的类型和 Activity 的类型不匹配
            if (!typeUtils.isSubtype(typeElement.asType(),activityType.asType())){
                throw new RuntimeException("@Paramter注解目前只能应用与 Activity");
            }

            // 获取类名
            ClassName className = ClassName.get(typeElement);
            // 方法体内容构造
            ParameterFactory factory = new ParameterFactory.Builder(parameterSpec)
                    .setMessager(messager)
                    .setElementUtils(elementUtils)
                    .setTypeUtils(typeUtils)
                    .setClassName(className)
                    .build();
            // 添加方法体内容的第一行
            factory.addFirstStatement();

            // 遍历类里面的所有属性
            for (Element fieldElement : entry.getValue()) {
                factory.buildStatement(fieldElement);
            }

            // 最终生成的类文件名
            String finalClassName = typeElement.getSimpleName() + Constants.PARAMETER_FILE_NAME;
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成获取参数类文件: " + className.packageName() + "." + finalClassName);

            // 生成类文件
            JavaFile.builder(className.packageName(), // 包名
                    TypeSpec.classBuilder(finalClassName) // 类名
                            .addSuperinterface(ClassName.get(parameterType)) // 实现接口
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(factory.build()) // 方法的构建
                            .build())
                    .build()
                    .writeTo(filter); // 文件生成器开始生成类文件
        }
    }

    /**
     * 解析所有添加了 @ARouter 注解的集合
     *
     * @param elements
     */
    private void valueOfParameterMap(Set<? extends Element> elements) {

        // 获取 Activity 类型
        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);

        // 显示类信息
        TypeMirror activityMirror = activityType.asType();
        for (Element element : elements) {
            // 注解的属性，父节点是类节点
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            if (tempParameterMap.containsKey(typeElement)) {
                tempParameterMap.get(typeElement)
                        .add(element);
            } else {
                List<Element> fields = new ArrayList<>();
                fields.add(element);
                tempParameterMap.put(typeElement, fields);
            }
        }
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Parameter.class.getCanonicalName());
        return annotations;
    }
}