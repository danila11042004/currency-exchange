package currEx.sevice;

import currEx.dao.CurrencyDao;
import currEx.dto.CurrencyRequestDto;
import currEx.dto.CurrencyResponseDto;
import currEx.entity.CurrencyEntity;
import currEx.mapper.CurrencyMapper;

import java.util.ArrayList;
import java.util.List;

public class CurrencyService {

    private final CurrencyDao currencyDao=new CurrencyDao();

    public List<CurrencyResponseDto> getAllCurrencies(){
        List<CurrencyEntity> listCurrencyEntity=currencyDao.findAll();
        List<CurrencyResponseDto> listCurrencyResponseDto=new ArrayList<>();
        for(CurrencyEntity currencyEntity:listCurrencyEntity){
            CurrencyResponseDto dto= CurrencyMapper.MAPPER.toDto(currencyEntity);
            listCurrencyResponseDto.add(dto);
        }
        return listCurrencyResponseDto;
    }
    public CurrencyResponseDto getCurrency(String code){
        CurrencyEntity currencyEntity=currencyDao.findByCode(code);
        return CurrencyMapper.MAPPER.toDto(currencyEntity);
    }
    public CurrencyResponseDto createCurrency(CurrencyRequestDto dto){
        CurrencyEntity currencyEntity=CurrencyMapper.MAPPER.toEntity(dto);
        currencyEntity=currencyDao.save(currencyEntity);
        return CurrencyMapper.MAPPER.toDto(currencyEntity);
    }

}
