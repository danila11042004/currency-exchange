package currEx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CurrencyRequestDto(
        @JsonProperty("name") String name,
        @JsonProperty("code") String code,
        @JsonProperty("sign") String sign) {}
