package com.nfx.work.frontdto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ExchangeDto {
    private String pesel;
    private BigDecimal money;
    private String currencyFrom;
    private String currencyTo;
}
