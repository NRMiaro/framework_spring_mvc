package framework.utils;

import java.lang.reflect.Method;

public class MethodDescriptor {
    private final Class<?> ownerClass;
    private final Method method;

    public MethodDescriptor(Class<?> ownerClass, Method method) {
        this.ownerClass = ownerClass;
        this.method = method;
    }

    public Class<?> getOwnerClass() {
        return ownerClass;
    }

    public Method getMethod() {
        return method;
    }
}
