package com.wangyi.annotation.model;

import javax.lang.model.element.Element;

/**
 * @Author lihl
 * @Date 2022/2/18 14:40
 * @Email 1601796593@qq.com
 */
public class RouterBean {

    public RouterBean(Builder builder) {
        this.element = builder.element;
        this.path = builder.path;
        this.group = builder.group;
    }

    public RouterBean(Type type, Class<?> clazz, String path, String group) {
        this.type = type;
        this.clazz = clazz;
        this.group = group;
        this.path = path;
    }

    public static RouterBean create(Type type, Class<?> clazz, String path, String group) {
        return new RouterBean(type, clazz, path, group);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Type getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getGroup() {
        return group;
    }

    public String getPath() {
        return path;
    }

    public enum Type {
        ACTIVITY,
        CALL
    }

    // 枚举类型
    private Type type;
    // 类节点
    private Element element;
    // 被 ARouter 注解的类
    private Class<?> clazz;
    // 路由组名
    private String group;
    // 路由的地址
    private String path;

    @Override
    public String toString() {
        return "RouterBean{" +
                "group='" + group + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public final static class Builder {
        // 类节点
        private Element element;
        // 路由组名
        private String group;
        // 路由的地址
        private String path;

        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public RouterBean build() {
            if (path == null || path.length() == 0) {
                throw new IllegalArgumentException("path 配置不能为空，如 /app/MainActivity");
            }
            return new RouterBean(this);
        }
    }
}
