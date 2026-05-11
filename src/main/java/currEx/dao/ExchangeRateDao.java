package currEx.dao;

import currEx.entity.CurrencyEntity;
import currEx.entity.ExchangeRateEntity;
import exeptions.DatabaseUnavailableException;
import exeptions.ExchangeRateAlreadyExistsException;
import exeptions.ExchangeRateNotFoundException;
import utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    public ExchangeRateEntity update(ExchangeRateEntity eRateEntity) {
        String query = """
                UPDATE ExchangeRates
                SET Rate=?
                WHERE BaseCurrencyId=? AND TargetCurrencyId=?
                RETURNING id
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(query)) {
            prepStatement.setBigDecimal(1, eRateEntity.getRate());
            prepStatement.setLong(2, eRateEntity.getBaseCurrency().getId());
            prepStatement.setLong(3, eRateEntity.getTargetCurrency().getId());
            try (ResultSet resultSet = prepStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    eRateEntity.setId(id);
                    return eRateEntity;
                }
                throw new ExchangeRateNotFoundException("Валютная пара отсутствует в базе данных");
            }
        } catch (SQLException e) {

            throw new DatabaseUnavailableException("База данных недоступна", e);
        }
    }

    public ExchangeRateEntity save(ExchangeRateEntity eRateEntity) {
        String query = "INSERT INTO ExchangeRates (BaseCurrencyId,TargetCurrencyId,Rate) VALUES (?,?,?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            prepStatement.setLong(1, eRateEntity.getBaseCurrency().getId());
            prepStatement.setLong(2, eRateEntity.getTargetCurrency().getId());
            prepStatement.setBigDecimal(3, eRateEntity.getRate());
            try {
                prepStatement.executeUpdate();
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE constraint failed")) {
                    throw new ExchangeRateAlreadyExistsException("Валютная пара с таким кодом уже существует", e);
                }
                throw new DatabaseUnavailableException("База данных недоступна", e);
            }
            try (ResultSet resultSet = prepStatement.getGeneratedKeys()) {
                resultSet.next();
                Long id = resultSet.getLong(1);
                eRateEntity.setId(id);
                return eRateEntity;
            }
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("База данных недоступна", e);
        }
    }

    public List<ExchangeRateEntity> findAll() {
        String query = """
                SELECT
                    er.id,
                    er.rate,
                    ba.id AS baseId,
                    ba.code AS baseCode,
                    ba.fullname AS baseName,
                    ba.sign AS baseSign,
                    ta.id AS targetId,
                    ta.code AS targetCode,
                    ta.fullname AS targetName,
                    ta.sign AS targetSign
                FROM ExchangeRates er
                JOIN Currencies ba ON er.baseCurrencyId = ba.id
                JOIN Currencies ta ON er.targetCurrencyId = ta.id;
                """;
        List<ExchangeRateEntity> eRateEntityList = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                eRateEntityList.add(resultToEntity(resultSet));
            }
            return eRateEntityList;
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("База данных недоступна", e);
        }
    }

    public ExchangeRateEntity findByCodes(String baseCode,String targetCode) {
        String query = """
                SELECT
                    er.id,
                    er.rate,
                    ba.id AS baseId,
                    ba.code AS baseCode,
                    ba.fullname AS baseName,
                    ba.sign AS baseSign,
                    ta.id AS targetId,
                    ta.code AS targetCode,
                    ta.fullname AS targetName,
                    ta.sign AS targetSign
                FROM ExchangeRates er 
                JOIN Currencies ba ON er.baseCurrencyId = ba.id
                JOIN Currencies ta ON er.targetCurrencyId = ta.id
                WHERE ba.code=? AND ta.code=?
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(query)
        ) {
            prepStatement.setString(1, baseCode);
            prepStatement.setString(2, targetCode);
            try (ResultSet resultSet = prepStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultToEntity(resultSet);
                }
                throw new ExchangeRateNotFoundException("Обменный курс для пары не найден");
            }

        } catch (SQLException e) {
            throw new DatabaseUnavailableException("База данных недоступна", e);
        }
    }

    public List<ExchangeRateEntity> findAllRateByCodes(String baseCode, String targetCode
            , String additionalCode ) {
        String query = """ 
                   SELECT
                   er.id,
                   er.rate,
                   ba.id AS baseId,
                   ba.code AS baseCode,
                   ba.fullname AS baseName,
                   ba.sign AS baseSign,
                   ta.id AS targetId,
                   ta.code AS targetCode,
                   ta.fullname AS targetName,
                   ta.sign AS targetSign
                   FROM ExchangeRates er 
                   JOIN Currencies ba ON er.baseCurrencyId = ba.id
                   JOIN Currencies ta ON er.targetCurrencyId = ta.id
                   WHERE ba.code IN (?,?,?) AND ta.code IN (?,?,?)
                   """;
        List<ExchangeRateEntity> eRateEntityList = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement prepStatement = connection.prepareStatement(query)
        ) {
            prepStatement.setString(1,baseCode);
            prepStatement.setString(2, targetCode);
            prepStatement.setString(3, additionalCode);
            prepStatement.setString(4, baseCode);
            prepStatement.setString(5, targetCode);
            prepStatement.setString(6, additionalCode);
            try (ResultSet resultSet = prepStatement.executeQuery()) {
                while(resultSet.next()) {
                    eRateEntityList.add(resultToEntity(resultSet));
                }
                return eRateEntityList;
            }
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("База данных недоступна", e);
        }
    }
    private ExchangeRateEntity resultToEntity(ResultSet resultSet) throws SQLException {
        return ExchangeRateEntity.builder()
                .id(resultSet.getLong("id"))
                .baseCurrency(CurrencyEntity.builder()
                        .id(resultSet.getLong("baseId"))
                        .code(resultSet.getString("baseCode"))
                        .fullName(resultSet.getString("baseName"))
                        .sign(resultSet.getString("baseSign"))
                        .build())
                .targetCurrency(CurrencyEntity.builder()
                        .id(resultSet.getLong("targetId"))
                        .code(resultSet.getString("targetCode"))
                        .fullName(resultSet.getString("targetName"))
                        .sign(resultSet.getString("targetSign"))
                        .build())
                .rate(resultSet.getBigDecimal("rate"))
                .build();
    }
}
