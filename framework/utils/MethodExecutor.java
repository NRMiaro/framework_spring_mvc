package framework.utils;

import java.lang.reflect.Method;

import framework.core.ControllerManager;

public class MethodExecutor {

    public static Object executeMethod(MethodDescriptor methodDescriptor, Object[] args) 
    throws Exception {
        Method method = methodDescriptor.getMethod();
        Class<?> ownerClass = methodDescriptor.getOwnerClass();
        Object controller = ControllerManager.getController(ownerClass);
        return method.invoke(controller, args);
    }

    public static Object executeMethod(MethodDescriptor md) throws Exception {
        return executeMethod(md, new Object[0]);
    }
}