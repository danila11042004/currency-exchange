package exeptions;

import java.sql.SQLException;

public class DatabaseUnavailableException extends RuntimeException{
    public DatabaseUnavailableException(String message, Throwable e){
        super(message,e);
    }
}
