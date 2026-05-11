package currEx.servlet;

import currEx.dto.CurrencyRequestDto;
import currEx.sevice.CurrencyService;
import currEx.dto.CurrencyResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import exeptions.RequiredFieldMissingException;
import utils.JsonUtil;
import utils.Validator;


import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService=new CurrencyService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");
        if (!Validator.isName(name) || !Validator.isCode(code)  || !Validator.isSign(sign)) {
            throw new RequiredFieldMissingException("Отсутствует нужное поле формы");
        }
        CurrencyRequestDto currencyRequestDto = new CurrencyRequestDto(name, code, sign);
        CurrencyResponseDto currencyResponseDto = currencyService.createCurrency(currencyRequestDto);
        response.setStatus(HttpServletResponse.SC_CREATED);
        JsonUtil.writeJson(response, currencyResponseDto);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        List<CurrencyResponseDto> listCurrencyResponseDto = currencyService.getAllCurrencies();
        response.setStatus(HttpServletResponse.SC_OK);
        JsonUtil.writeJson(response, listCurrencyResponseDto);
    }
}
