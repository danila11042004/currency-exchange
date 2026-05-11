package currEx.dao;


import currEx.entity.CurrencyEntity;
import exeptions.CurrencyAlreadyExistsException;
import exeptions.CurrencyNotFoundException;
import exeptions.DatabaseUnavailableException;
import utils.DatabaseManager;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {


    public List<CurrencyEntity> findAll() {
        String query = "SELECT * FROM Currencies";
        List<CurrencyEntity> listCurrencyEntity = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                CurrencyEntity currencyEntity = resultToEntity(resultSet);
                listCurrencyEntity.add(currencyEntity);
            }
            return listCurrencyEntity;
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("База данных недоступна",e);
        }
    }

    public CurrencyEntity findByCode(String code) {
        String query = "SELECT * FROM Currencies WHERE Code = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(query))
        {
            prepStatement.setString(1,code);
            try(ResultSet resultSet = prepStatement.executeQuery()){
                if(resultSet.next()){
                    return resultToEntity(resultSet);
                }
                throw new CurrencyNotFoundException("Валюта не найдена");
            }
        }
        catch (SQLException e){
            throw new DatabaseUnavailableException("База данных недоступна",e);
        }
    }
    public CurrencyEntity save(CurrencyEntity currencyEntity){
        String query="INSERT INTO Currencies (FullName,Code,Sign) VALUES (?,?,?)";
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement prepStatement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS))
        {
            prepStatement.setString(1, currencyEntity.getFullName());
            prepStatement.setString(2, currencyEntity.getCode());
            prepStatement.setString(3, currencyEntity.getSign());
            try {
                prepStatement.executeUpdate();
            } catch (SQLException e) {
                if(e.getMessage().contains("UNIQUE constraint failed")){
                    throw new CurrencyAlreadyExistsException("Валюта с таким кодом уже существует",e);
                }
                throw new DatabaseUnavailableException("База данных недоступна",e);
            }
            try(ResultSet resultSet = prepStatement.getGeneratedKeys()){
                resultSet.next();
                Long id=resultSet.getLong(1);
                currencyEntity.setId(id);
                return currencyEntity;
            }

        }
        catch (SQLException e){
            throw new DatabaseUnavailableException("База данных недоступна",e);
        }
    }

    private CurrencyEntity resultToEntity(ResultSet resultSet) throws SQLException{
        return  CurrencyEntity.builder()
                .id(resultSet.getLong("id"))
                .code(resultSet.getString("Code"))
                .fullName(resultSet.getString("FullName"))
                .sign(resultSet.getString("Sign"))
                .build();
    }
}