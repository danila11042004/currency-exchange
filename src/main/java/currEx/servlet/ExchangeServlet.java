package currEx.servlet;

import currEx.dto.ExchangeResponseDto;
import currEx.sevice.ExchangeRateService;
import exeptions.RequiredFieldMissingException;
import utils.JsonUtil;
import utils.Validator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String baseCode=request.getParameter("from");
        String targetCode=request.getParameter("to");
        String strAmount=request.getParameter("amount");
        Optional<BigDecimal> optionalAmount=Validator.isAmount(strAmount);
        if (!Validator.isCode(baseCode) || !Validator.isCode(targetCode) || optionalAmount.isEmpty()) {
            throw new RequiredFieldMissingException("Отсутствует нужное поле формы");
        }
        BigDecimal amount=optionalAmount.get();
        ExchangeResponseDto listExchangeResponse = exchangeRateService
                .exchange(baseCode,targetCode,amount);
        response.setStatus(HttpServletResponse.SC_OK);
        JsonUtil.writeJson(response, listExchangeResponse);
    }

}
