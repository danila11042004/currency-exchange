package utils;

import exeptions.RequiredFieldMissingException;

import java.math.BigDecimal;
import java.util.Optional;

public class Validator {
    public static boolean isCode(String code){
        return code != null && code.matches("[A-Za-z]{3}");
    }
    public static boolean isName(String name){
        return name != null && name.length()<=250&& !name.isEmpty();
    }
    public static boolean isSign(String sign){
        return sign != null && sign.length()<=10 && !sign.isEmpty();
    }
    public static Optional<BigDecimal> isRate(String strRate){
        if(strRate!=null){
            try {
                BigDecimal rate=new BigDecimal(strRate);
                if(rate.signum()>0){
                    return Optional.of(rate);
                }
            }
            catch (NumberFormatException e){
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    public static Optional<BigDecimal> isAmount(String strAmount){
        return isRate(strAmount);
    }
    public static Optional<String> isCodeInPath(String path){
        if(path != null&&path.length()==4){
            String code=path.substring(1);
            if(code.matches("[A-Za-z]{3}")){
                return Optional.of(code);
            }
        }
        return Optional.empty();
    }
    public static boolean isCodesInPath(String path){
        if(path != null&&path.length()==7){
            String baseCode = path.substring(1, 4);
            String targetCode = path.substring(4, 7);
            return baseCode.matches("[A-Za-z]{3}") && targetCode.matches("[A-Za-z]{3}");
        }
        return false;
    }

    public static BigDecimal getParameter(String body, String parameter) {
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int disjunctiveIndex = pair.indexOf("=");
            if (disjunctiveIndex > 0) {
                if (pair.substring(0, disjunctiveIndex).equals(parameter)) {
                    try {
                        String value=pair.substring(disjunctiveIndex + 1);
                        if(!value.isEmpty()){
                            BigDecimal rate=new BigDecimal(pair.substring(disjunctiveIndex + 1));
                            if(rate.signum()>0){
                                return rate;
                            }
                        }
                    } catch (NumberFormatException e) {
                        throw new RequiredFieldMissingException("Отсутствует нужное поле формы");
                    }
                }
            }
        }
        throw new RequiredFieldMissingException("Отсутствует нужное поле формы");
    }
}
