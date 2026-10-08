package framework.utils;

import java.lang.reflect.Method;

public class MethodExecutor {
    
    public static Object executeMethod(MethodDescriptor methodDescriptor) throws Exception {
        Method method = methodDescriptor.getMethod();
        Class<?> ownerClass = methodDescriptor.getOwnerClass();

        Object returnValue = method.invoke(
            ownerClass.getDeclaredConstructor().newInstance(), 
            new Object[0]
        ); 
        return returnValue;
    }

}
