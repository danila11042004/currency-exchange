package currEx.servlet;

import currEx.dto.ExchangeRateRequestDto;
import currEx.dto.ExchangeRateResponseDto;
import currEx.sevice.ExchangeRateService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import exeptions.RequiredFieldMissingException;
import utils.JsonUtil;
import utils.Validator;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String baseCode = request.getParameter("baseCurrencyCode");
        String targetCode = request.getParameter("targetCurrencyCode");
        String strRate = request.getParameter("rate");
        Optional<BigDecimal> optionalRate = Validator.isRate(strRate);
        if (!Validator.isCode(baseCode) || !Validator.isCode(targetCode) || optionalRate.isEmpty()) {
            throw new RequiredFieldMissingException("Отсутствует нужное поле формы");
        }
        BigDecimal rate = optionalRate.get();
        ExchangeRateRequestDto eRateRequestDto = new ExchangeRateRequestDto(baseCode, targetCode, rate);
        ExchangeRateResponseDto eRateResponseDto = exchangeRateService.create(eRateRequestDto);
        response.setStatus(HttpServletResponse.SC_CREATED);
        JsonUtil.writeJson(response, eRateResponseDto);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        List<ExchangeRateResponseDto> listExchangeRateResponse = exchangeRateService.getExchangeRates();
        response.setStatus(HttpServletResponse.SC_OK);
        JsonUtil.writeJson(response, listExchangeRateResponse);
    }
}
