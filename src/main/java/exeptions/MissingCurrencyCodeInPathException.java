package exeptions;

public class MissingCurrencyCodeInPathException extends RuntimeException{
    public MissingCurrencyCodeInPathException(String message){
        super(message);
    }
}
