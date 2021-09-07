package com.nfx.work.frontdto;

import com.nfx.work.validator.ValidUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ValidUser
public class RegisterUser {
    private String name;
    private String surname;
    private String pesel;
    private BigDecimal money;

}
