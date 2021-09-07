package com.nfx.work.service;

import com.nfx.work.frontdto.RegisterUser;
import com.nfx.work.frontdto.RegisterUserDto;
import com.nfx.work.frontdto.UserDictionary;
import com.nfx.work.mapper.UserMapper;
import com.nfx.work.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final NbpService nbpService;

    public RegisterUserDto createUser(RegisterUser user) {
        userRepository.findByPesel(user.getPesel()).ifPresent(it -> {
            throw new RuntimeException("User exists in in our databases");
        });
        return mapper.map(userRepository.saveAndFlush(mapper.map(user)),"User register success",true);
    }

    public UserDictionary getDictionary() {
        return new UserDictionary()
                .setCurrency(nbpService.getCurrency().keySet().stream().sorted().collect(Collectors.toList()));
    }
}
