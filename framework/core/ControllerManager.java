package framework.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerManager {

    private static final Map<Class<?>, Object> controllers =
            new ConcurrentHashMap<>();

    public static Object getController(Class<?> controllerClass)
            throws Exception {

        return controllers.computeIfAbsent(controllerClass, clazz -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(
                    "Impossible d'instancier le Controller : "
                    + clazz.getName(), e
                );
            }
        });
    }
}