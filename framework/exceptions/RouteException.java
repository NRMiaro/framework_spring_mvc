package framework.exceptions;

import java.lang.reflect.Method;

import framework.http.Route;

public class RouteException extends ScanException {

    private RouteException(String message) {
        super(message);
    }

    public RouteException(Route route, Method controllerMethod) {
        super("Route " + route.getHttpMethod().toString() + route.getUrl() + " utilisee a plusierus reprises");
    }
    
}
