package framework.http;

public enum HttpMethod {
    GET, 
    POST, 
    PUT, 
    DELETE;

    public static HttpMethod from(String methodName){
        switch (methodName.toUpperCase().trim()) {
            case "GET":
                return GET;

            case "POST":
                return POST;
            
            case "PUT":
                return PUT;

            case "DELETE":
                return DELETE;
        
            default:
                return null;
        }
    }
}
