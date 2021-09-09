package com.nfx.work.frontdto;

import com.nfx.work.validator.ValidUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ValidUser
public class UserDictionary {
    private List<String> currency;
    private List<String> pesels;
}
