package framework.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

import framework.annotations.Controller;
import framework.annotations.Model;
import framework.annotations.UrlMapping;
import framework.core.MapFile;
import framework.core.ModelAndView;
import framework.core.ViewNameHandler;
import framework.exceptions.RouteException;
import framework.exceptions.ScanException;
import framework.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletUtils {

    // Retourne le path relatif a l'application, sans le context path, sans query
    // string
    // ex: requete vers http://localhost:8080/monapp/user?id=5
    // -> retourne "/user"
    public static String getRequestPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        return uri.substring(contextPath.length());
    }

    public static void scanUrlsIn(
            Class<?> controllerClass,
            Map<Route, MethodDescriptor> existingRouteMapper) {
        // verifier qu'on scanne bien un controller
        if (!controllerClass.isAnnotationPresent(Controller.class))
            throw new ScanException("Erreur lors du scan: " + controllerClass.getName() + " n'est pas un Controller");

        // traiter chaque method de la class et voir si annotee @UrlMapping
        for (Method method : controllerClass.getDeclaredMethods()) {
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
                    methodDescriptor);
        }
    }

    public static void writeHtml(HttpServletResponse res, String body) throws IOException {
        write(res, "text/html;charset=UTF-8", "<!DOCTYPE html><html><body>" + body + "</body></html>");
    }

    public static void writeJson(HttpServletResponse res, String json) throws IOException {
        write(res, "application/json;charset=UTF-8", json);
    }

    private static void write(HttpServletResponse res, String contentType, String content) throws IOException {
        res.setContentType(contentType);
        try (PrintWriter out = res.getWriter()) {
            out.println(content);
        }
    }

    public static String routesToHtml(Map<Route, MethodDescriptor> routes) {
        StringBuilder sb = new StringBuilder("<h3>Routes connues :</h3><ul>");
        routes.forEach((r, md) -> sb.append("<li>").append(r.getHttpMethod()).append(" ").append(r.getUrl())
                .append(" -> ").append(md.getOwnerClass().getName())
                .append(".").append(md.getMethod().getName()).append("</li>"));
        return sb.append("</ul>").toString();
    }

    // Transfère les attributs du ModelAndView puis forward vers la vue
    public static void render(ModelAndView mv, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (mv.getAttributes() != null) {
            mv.getAttributes().forEach((k, v) -> {
                if (k != null)
                    req.setAttribute(k, v);
            });
        }
        req.getRequestDispatcher(ViewNameHandler.resolve(mv.getPageName())).forward(req, res);
    }


    public static Object[] getParamValues(Method method, HttpServletRequest req) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            // On cree l'objet annote @Model 
            if (p.isAnnotationPresent(Model.class)) {
                args[i] = ParameterConverter.bindObject(p.getType(), req);
            } 
            // On cree l'objet MapFile 
            // si le developpeur l'appelle directement depuis le controller 
            else if (p.getType().equals(MapFile.class)){
                args[i] = FileConverter.convert(p.getName(), req);
            } else {
                String value = req.getParameter(p.getName());
                args[i] = ParameterConverter.convert(value, p.getType());
            }
        }

        return args;
    }
}