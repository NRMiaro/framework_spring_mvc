package framework.http;

import java.util.Objects;

public class Route {
    private String url;
    private HttpMethod httpMethod;

    public Route(){}

    // on pose la methode GET par defaut si omis 
    // PS: regle changeable 
    public Route(String url){
        this(url, HttpMethod.GET);
    }

    public Route(String url, HttpMethod method){
        setUrl(url);
        setHttpMethod(method);
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public int hashCode(){
        return Objects.hash(url, httpMethod);
    }


    @Override 
    public boolean equals(Object o){
        if (this == o) 
            return true;
        if (o == null || getClass() != o.getClass()) 
            return false;
        Route route = (Route) o;
        return Objects.equals(route.getUrl(), this.url) && 
            route.getHttpMethod() == this.httpMethod;
    }

}
