package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;


public class JsonUtil {

    private static final ObjectMapper objectMapper=new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static void writeJson(HttpServletResponse response, Object object) throws IOException {
        objectMapper.writeValue(response.getWriter(),object);
    }

    //на всякий, в коде не используется
    public static <T> T readJson(HttpServletRequest request, Class<T> dtoClass) throws IOException {
        return objectMapper.readValue(request.getReader(),dtoClass);
    }
}
