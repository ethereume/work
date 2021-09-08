package com.nfx.work.controller;

import com.nfx.work.frontdto.*;
import com.nfx.work.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;


    @GetMapping("all-dictionary")
    public UserDictionary getDictionary() {
        return userService.getDictionary();
    }

    @GetMapping("account/{iban}")
    public AccountDto getAccount(@PathVariable("iban") String iban){ return userService.getAccountByIban(iban);}

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserDto registerUser(@Valid @RequestBody RegisterUser registerUser) {
        return userService.createUser(registerUser);
    }

    @PostMapping("exchange")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExchangeResponseDto exchange(@RequestBody ExchangeDto exchange){
        return userService.exchange(exchange);
    }
}
