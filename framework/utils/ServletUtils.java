package framework.utils;

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
}