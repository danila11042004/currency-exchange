package currEx.servlet;

import currEx.sevice.CurrencyService;
import currEx.dto.CurrencyResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import exeptions.MissingCurrencyCodeInPathException;
import utils.JsonUtil;
import utils.Validator;


import java.io.IOException;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService=new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        String path= request.getPathInfo();
        Optional<String> optionalCode=Validator.isCodeInPath(path);
        if(optionalCode.isEmpty()){
            throw new MissingCurrencyCodeInPathException("Код валюты отсутствует в адресе");
        }
        CurrencyResponseDto currencyResponseDto= currencyService.getCurrency(optionalCode.get());
        response.setStatus(HttpServletResponse.SC_OK);
        JsonUtil.writeJson(response,currencyResponseDto);
    }
}
