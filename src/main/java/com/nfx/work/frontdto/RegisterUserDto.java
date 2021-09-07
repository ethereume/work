package com.nfx.work.frontdto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class RegisterUserDto {
    private String name;
    private String surname;
    private String message;
    private Boolean created;
}
