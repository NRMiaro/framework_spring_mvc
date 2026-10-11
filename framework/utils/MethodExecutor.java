package framework.utils;

import java.lang.reflect.Method;

import framework.annotations.ApiRest;

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

    public static String executeApiRestMethod(MethodDescriptor methodDescriptor) throws Exception {
        if (!methodDescriptor.getMethod().isAnnotationPresent(ApiRest.class))
            throw new Exception("La methode executee n'est pas une REST API");

        Object returnValue = executeMethod(methodDescriptor);
        String jsonResult = null;
        if (returnValue instanceof String r)
            jsonResult = r;
        else jsonResult = (new JsonConverter()).toJson(returnValue);

        return jsonResult;
    }

}
