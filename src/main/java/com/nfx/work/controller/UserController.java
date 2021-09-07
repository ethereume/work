package com.nfx.work.controller;

import com.nfx.work.frontdto.RegisterUser;
import com.nfx.work.frontdto.RegisterUserDto;
import com.nfx.work.frontdto.UserDictionary;
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

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserDto registerUser(@Valid @RequestBody RegisterUser registerUser) {
        return userService.createUser(registerUser);
    }
}
