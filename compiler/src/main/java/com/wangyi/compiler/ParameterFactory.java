package com.wangyi.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.wangyi.annotation.Parameter;
import com.wangyi.compiler.utils.Constants;
import com.wangyi.compiler.utils.EmptyUtils;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @Author lihl
 * @Date 2022/2/20 7:56
 * @Email 1601796593@qq.com
 */
public class ParameterFactory {

    // MainActivity t = (MainActivity) target;
    private static final String CONTENT = "$T t = ($T) target";

    // 方法体构建
    private MethodSpec.Builder methodBuilder;

    // type (类信息) 工具类
    private Types typeUtils;

    // 获取元素接口信息（生成类文件需要的接口实现类）
    private TypeMirror callMirror;

    // 用来输出警告、错误等日志
    private Messager messager;

    // 类名 如 : MainActivity
    private ClassName className;

    public ParameterFactory(Builder builder) {
        this.messager = builder.messager;
        this.className = builder.className;
        this.typeUtils = builder.typeUtils;

        methodBuilder = MethodSpec.methodBuilder(Constants.PARAMETER_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.parameterSpec);

        callMirror = builder.elementUtils
                .getTypeElement(Constants.CALL)
                .asType();
    }

    /**
     * 添加方法体内容的第一行
     * MainActivity t = (MainActivity) target;
     */
    public void addFirstStatement() {
        methodBuilder.addStatement(CONTENT,className,className);
    }

    /**
     * 构建方法体内容
     * t.s = t.getIntent.getStringExtra("s")
     * @param element
     */
    public void buildStatement(Element element) {
        // 遍历注解的属性节点，生成函数体
        TypeMirror typeMirror = element.asType();
        // 获取 TypeKind 枚举类型的序列号
        int type = typeMirror.getKind().ordinal();
        // 获取属性名
        String fieldName = element.getSimpleName().toString();
        // 获取注解的值
        String annotationValue = element.getAnnotation(Parameter.class).name();
        // 注解值为空的情况
        annotationValue = EmptyUtils.isEmpty(annotationValue) ? fieldName : annotationValue;

        String finalValue = "t." + fieldName;
        // t.s = t.getIntent().
        String methodContent = finalValue + " = t.getIntent().";

        messager.printMessage(Diagnostic.Kind.NOTE,"type >>>" + type);

        // TypeKind 枚举类型不支持String
        if (type == TypeKind.INT.ordinal()){
            methodContent += "getIntExtra($S, "+finalValue+")";
        }else if (type == TypeKind.BOOLEAN.ordinal()){
            methodContent += "getBooleanExtra($S, "+finalValue+")";
        }else if (type == TypeKind.DOUBLE.ordinal()){
            methodContent += "getDoubleExtra($S, "+finalValue+")";
        }else{
            if (typeMirror.toString().equalsIgnoreCase(Constants.STRING)) {
                methodContent += "getStringExtra($S)";
            }else if (typeUtils.isSubtype(typeMirror,callMirror)){
                // isSubtype 相当于 instanceOf
                methodContent = "t."+fieldName + " = ($T) $T.getInstance().build($S).navigation(t)";
                methodBuilder.addStatement(methodContent,
                        TypeName.get(typeMirror),
                        ClassName.get(Constants.BASE_PACKAGE,Constants.ROUTER_MANAGER),
                        annotationValue);
                return;
            }
        }

        if (methodContent.endsWith(")")){
            // 添加最终方法拼接语句
            methodBuilder.addStatement(methodContent,annotationValue);
        }else{
            messager.printMessage(Diagnostic.Kind.ERROR,"目前暂不只支持 String,int,boolean 传参");
        }
    }

    public MethodSpec build() {
        return methodBuilder.build();
    }

    public static class Builder {

        public ParameterSpec parameterSpec;

        // 操作 Element 工具类
        private Elements elementUtils;

        // type (类信息) 工具类，包含用于操作 TypeMirror 的工具方法
        private Types typeUtils;

        // 用来输出警告、错误等日志
        private Messager messager;

        // 类名
        private ClassName className;

        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }

        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }

        public Builder setElementUtils(Elements elementUtils) {
            this.elementUtils = elementUtils;
            return this;
        }

        public Builder setTypeUtils(Types typeUtils) {
            this.typeUtils = typeUtils;
            return this;
        }

        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }

        public ParameterFactory build() {
            if (parameterSpec == null){
                throw new IllegalArgumentException("parameterSpec 方法参数体为空");
            }
            if (className == null){
                throw new IllegalArgumentException("方法内容中的className为空");
            }
            if (messager == null){
                throw new IllegalArgumentException("messager 为空，messager用来报告错误，警告和其他提示信息");
            }

            return new ParameterFactory(this);
        }

    }
}
