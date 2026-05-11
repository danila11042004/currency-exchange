package exeptions;

public class ExchangeNotPossibleException extends RuntimeException {
    public ExchangeNotPossibleException(String message) {
        super(message);
    }
}
