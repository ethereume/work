package com.nfx.work.backenddto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RateResponse {
    private String table;
    private String no;
    private LocalDate tradingDate;
    private LocalDate effectiveDate;
    private List<Rate> rates;
}
