package framework.utils;

public class Rules {
    
    public static boolean isValidUrl(String URL){
        // doit commencer par un '/'
        if (!URL.startsWith("/"))
            return false;

        return true;
    }

}
