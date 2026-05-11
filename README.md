# Currency Exchange REST API

Серверное приложение для управления валютами и обменными курсами. Реализует REST API для просмотра, добавления и обновления валют, а также конвертации сумм из одной валюты в другую.

## 📋 Функциональные возможности

- **Валюты**:
    - `GET /currencies` – список всех валют
    - `GET /currency/{code}` – информация о конкретной валюте
    - `POST /currencies` – добавление новой валюты (поля формы: `name`, `code`, `sign`)
- **Обменные курсы**:
    - `GET /exchangeRates` – список всех курсов с вложенными объектами валют
    - `GET /exchangeRate/{base}{target}` – курс для конкретной пары (например, `/exchangeRate/USDRUB`)
    - `POST /exchangeRates` – создание нового курса (поля формы: `baseCurrencyCode`, `targetCurrencyCode`, `rate`)
    - `PATCH /exchangeRate/{base}{target}` – обновление курса (поле формы: `rate`)
- **Конвертация валюты**:
    - `GET /exchange?from={from}&to={to}&amount={amount}` – расчёт перевода суммы
    - Поддерживаются сценарии: прямой курс, обратный курс, кросс-курс через USD.

## Технологии

- **Java 17**
- **Jakarta Servlet 6.0** + **Tomcat 10**
- **SQLite** 
- **JDBC** + **HikariCP** 
- **Jackson** 
- **Lombok**, **MapStruct** 
- **Maven** 



### Требования
- JDK 17+
- Apache Maven 3.6+
- Tomcat 10 (локально или в IDE)

### Сборка
bash
git clone https://github.com/ваш_username/currency-exchange.git
cd currency-exchange
mvn clean package


## Локальный запуск (через IntelliJ IDEA):

1. Откройте проект в IDEA.
2. Настройте Tomcat 10 (в конфигурации Run/Debug).
3. Запустите сервер – IDEA автоматически развернёт приложение.
4. Приложение будет доступно по адресу: http://localhost:8080/currency-exchange/

## Через Maven + Tomcat (вручную):
1. mvn clean package
2. cp target/currency-exchange.war /path/to/tomcat/webapps/
3. cd /path/to/tomcat/bin
4. ./startup.sh   (или startup.bat на Windows)

##  Деплой на удалённый сервер

1. Установите Java 17 и Tomcat 10.
2. Скопируйте `target/currency-exchange.war` в `webapps/` Tomcat.
3. Убедитесь, что файл базы данных (`currencyExchange`) находится в `WEB-INF/classes` или настройте путь через `context.getRealPath()`.
4. Запустите Tomcat. Приложение будет доступно по `http://your-server:8080/currency-exchange/`.