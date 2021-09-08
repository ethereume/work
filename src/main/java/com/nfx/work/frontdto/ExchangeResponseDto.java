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
public class ExchangeResponseDto {
    private String currencyFrom;
    private BigDecimal left;
    private BigDecimal rate;
    private String currencyTo;
    private BigDecimal stay;
}
