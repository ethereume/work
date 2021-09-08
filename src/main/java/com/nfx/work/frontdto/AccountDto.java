package com.nfx.work.frontdto;

import com.nfx.work.validator.ValidUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ValidUser
public class AccountDto {
    private String iban;
    private String currency;
    private String user;
    private String surname;
    private BigDecimal money;
    private List<SubAccountDto> accounts;
}
