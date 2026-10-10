package framework.exceptions;

public class ReturnTypeException extends Exception {
    
    public ReturnTypeException(Class<?> returnTypeExcepted){
        super("Il faut retourner un objet de type " + returnTypeExcepted.getName());
    }

}
