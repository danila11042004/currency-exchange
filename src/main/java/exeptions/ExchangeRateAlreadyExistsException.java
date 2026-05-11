package exeptions;

public class ExchangeRateAlreadyExistsException extends RuntimeException{
    public ExchangeRateAlreadyExistsException(String message,Throwable e) {
        super(message,e);
    }
}
