package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DatabaseManager{
    private static HikariDataSource dataSource;
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void init(ServletContext context){
        if(dataSource!=null){return;}
        String dbPath=context.getRealPath("/WEB-INF/classes/currencyExchange");
        HikariConfig config=new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:"+dbPath);
        config.setMaximumPoolSize(1);
        dataSource=new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    public static void closePool(){
        if(dataSource!=null){
            dataSource.close();
        }
    }
}
