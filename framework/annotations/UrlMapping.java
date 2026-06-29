package framework.annotations;

import java.lang.annotation.*;

import framework.http.HttpMethod;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlMapping {
    String url();
    HttpMethod httpMethod() default HttpMethod.GET;
}
