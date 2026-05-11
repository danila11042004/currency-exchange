package currEx.entity;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class CurrencyEntity {
    private Long id;
    private String fullName;
    private String code;
    private String sign;
}
