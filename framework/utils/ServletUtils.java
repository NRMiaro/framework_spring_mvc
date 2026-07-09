package framework.utils;

import java.lang.reflect.Method;
import java.util.Map;

import framework.annotations.Controller;
import framework.annotations.UrlMapping;
import framework.exceptions.RouteException;
import framework.exceptions.ScanException;
import framework.http.*;
import jakarta.servlet.http.HttpServletRequest;

public class ServletUtils {

    // Retourne le path relatif a l'application, sans le context path, sans query string
    // ex: requete vers http://localhost:8080/monapp/user?id=5
    // -> retourne "/user"
    public static String getRequestPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        return uri.substring(contextPath.length());
    }

    public static void scanUrlsIn(
        Class<?> controllerClass,
        Map<Route, MethodDescriptor> existingRouteMapper
    ){
        // verifier qu'on scanne bien un controller
        if (!controllerClass.isAnnotationPresent(Controller.class))
            throw new ScanException("Erreur lors du scan: " + controllerClass.getName() + " n'est pas un Controller");

        // traiter chaque method de la class et voir si annotee @UrlMapping
        for (Method method : controllerClass.getDeclaredMethods()){
            UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);
            if (urlMapping == null)
                continue;

            String URL = urlMapping.url();
            HttpMethod httpMethod = urlMapping.httpMethod();
            Route route = new Route(URL, httpMethod);

            /**
             * On ne peut pas avoir plusieurs memes routes dans le code d'un projet
             * donc on leve une RouteException dans ce cas
             */
            if (existingRouteMapper.containsKey(route))
                throw new RouteException(route, method);

            MethodDescriptor methodDescriptor = new MethodDescriptor(controllerClass, method);
            existingRouteMapper.put(
                route,
                methodDescriptor
            );
        }
    }
}