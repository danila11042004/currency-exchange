package currEx.servlet;

import currEx.dto.ExchangeRateRequestDto;
import currEx.dto.ExchangeRateResponseDto;
import currEx.sevice.ExchangeRateService;
import exeptions.MissingCurrencyCodeInPathException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import utils.JsonUtil;
import utils.Validator;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends MyServlet {
    private static final String RATE_PARAMETER = "rate";
    private ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String path = request.getPathInfo();
        if (!Validator.isCodesInPath(path)) {
            throw new MissingCurrencyCodeInPathException("Коды валют пары отсутствуют в адресе");
        }
        String baseCode = path.substring(1, 4);
        String targetCode = path.substring(4, 7);
        ExchangeRateResponseDto eRateResponseDto = exchangeRateService
                .getExchangeRate(baseCode, targetCode);
        response.setStatus(HttpServletResponse.SC_OK);
        JsonUtil.writeJson(response, eRateResponseDto);
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String path = request.getPathInfo();
        if (!Validator.isCodesInPath(path)) {
            throw new MissingCurrencyCodeInPathException("Коды валют пары отсутствуют в адресе");
        }
        String baseCode = path.substring(1, 4);
        String targetCode = path.substring(4, 7);
        String body = request.getReader().lines().collect(Collectors.joining());
        BigDecimal rate = Validator.getParameter(body, RATE_PARAMETER);
        ExchangeRateRequestDto eRateRequestDto = new ExchangeRateRequestDto(baseCode, targetCode, rate);
        ExchangeRateResponseDto eRateResponseDto = exchangeRateService.updateRate(eRateRequestDto);
        response.setStatus(HttpServletResponse.SC_OK);
        JsonUtil.writeJson(response, eRateResponseDto);
    }

}
