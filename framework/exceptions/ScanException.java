package framework.exceptions;

public class ScanException extends RuntimeException {
    public ScanException(){
        
    }

    public ScanException(String message){
        super(message);
    }
}
