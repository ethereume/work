package com.nfx.work.frontdto;

import com.nfx.work.validator.ValidUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ValidUser
public class UserDto {
    private String fullName;
    private String pesel;
    private String iban;
}
