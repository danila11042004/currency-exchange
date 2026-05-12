package currEx.filter;

import exeptions.*;
import utils.JsonUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebFilter(urlPatterns = {"/currencies", "/exchangeRates", "/exchange","/currency/*",
        "/exchangeRate/*"})
public class ServletFilter implements Filter {
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String ENCODING_TYPE = "UTF-8";
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        request.setCharacterEncoding(ENCODING_TYPE);
        response.setContentType(JSON_CONTENT_TYPE);
        response.setCharacterEncoding(ENCODING_TYPE);
        try {
            filterChain.doFilter(request,response);
        }
        catch (Exception e){
            handleException(e,response);
        }
    }
    private void handleException(Exception e, HttpServletResponse response) {
        try {
            if(e instanceof DatabaseUnavailableException){
                e.getMessage();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JsonUtil.writeJson(response, Map.of("message", e.getMessage()));
            }
            else if(e instanceof MissingCurrencyCodeInPathException){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(response, Map.of("message",e.getMessage()));
            }
            else if(e instanceof RequiredFieldMissingException){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(response, Map.of("message", e.getMessage()));
            }
            else if(e instanceof CurrencyNotFoundException){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.writeJson(response, Map.of("message",e.getMessage()));
            }
            else if(e instanceof ExchangeRateNotFoundException){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.writeJson(response, Map.of("message",e.getMessage()));
            }
            else if(e instanceof CurrencyAlreadyExistsException){
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                JsonUtil.writeJson(response, Map.of("message", e.getMessage()));
            }
            else if(e instanceof ExchangeRateAlreadyExistsException){
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                JsonUtil.writeJson(response, Map.of("message", e.getMessage()));
            }
            else if(e instanceof ExchangeNotPossibleException){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.writeJson(response, Map.of("message", e.getMessage()));
            }
            else{
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JsonUtil.writeJson(response, Map.of("message", "Ошибка сервера"));
            }
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ex.printStackTrace();
        }
    }

}
