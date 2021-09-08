package com.nfx.work.service;

import com.nfx.work.entity.Account;
import com.nfx.work.entity.SubAccount;
import com.nfx.work.entity.User;
import com.nfx.work.frontdto.*;
import com.nfx.work.mapper.ApplicationMapper;
import com.nfx.work.repository.AccountRepository;
import com.nfx.work.repository.SubAccountRepository;
import com.nfx.work.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final SubAccountRepository subAccountRepository;
    private final ApplicationMapper mapper;
    private final NbpService nbpService;

    public RegisterUserDto createUser(RegisterUser user) {
        userRepository.findByPesel(user.getPesel()).ifPresent(it -> {
            throw new RuntimeException("User exists in in our databases");
        });
        return mapper.map(saveUser(user),"User register success",true);
    }

    public AccountDto getAccountByIban(String iban){
        Account account = accountRepository.findByIban(iban).orElseThrow(() -> new RuntimeException("This account doesn't exists !"));
        return mapper.map(account);
    }

    public UserDictionary getDictionary() {
        return new UserDictionary()
                .setCurrency(nbpService.getCurrency().keySet().stream().sorted().collect(Collectors.toList()));
    }

    @Transactional
    public ExchangeResponseDto exchange(ExchangeDto exchange) {
        User user = userRepository.findByPesel(exchange.getPesel()).orElseThrow(() -> new RuntimeException("User not exists in databases"));
        if(StringUtils.isBlank(exchange.getCurrencyFrom()) || StringUtils.isBlank(exchange.getCurrencyTo()) || Objects.isNull(exchange.getMoney())) {
            throw new RuntimeException("Parameter must be set");
        }
        Account account = user.getAccount();
        BigDecimal newMoney = BigDecimal.ZERO;
        BigDecimal newMoneyTo = BigDecimal.ZERO;
        if(exchange.getCurrencyFrom().equals("PLN")) {
            SubAccount subAccount = user.getAccount().getSubAccounts()
                    .stream()
                    .filter(it -> exchange.getCurrencyTo().toLowerCase().equals(it.getCurrency().toLowerCase()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("There is no account with this currency " + exchange.getCurrencyTo()));
            BigDecimal toExchange = exchange.getMoney().multiply(nbpService.getCurrency().get(exchange.getCurrencyTo()).getBid());
            if(account.getStartMoney().subtract(toExchange).compareTo(BigDecimal.ZERO) <= 0 ) {
                throw new RuntimeException("Not enough money");
            }
            newMoney = account.getStartMoney().subtract(toExchange);
            newMoneyTo = subAccount.getStartMoney().add(toExchange);
            accountRepository.saveAndFlush(account.setStartMoney(newMoney));
            subAccountRepository.saveAndFlush(subAccount.setStartMoney(newMoneyTo));
        } else {
            SubAccount accountFrom = user.getAccount().getSubAccounts()
                    .stream()
                    .filter(it -> exchange.getCurrencyFrom().equalsIgnoreCase(it.getCurrency()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("There is no account with this currency " + exchange.getCurrencyTo()));
            SubAccount accountTo = user.getAccount().getSubAccounts()
                    .stream()
                    .filter(it -> exchange.getCurrencyTo().equalsIgnoreCase(it.getCurrency()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("There is no account with this currency " + exchange.getCurrencyTo()));
            BigDecimal toExchange = exchange.getMoney().multiply(nbpService.getCurrency().get(exchange.getCurrencyTo()).getBid());
            if(accountFrom.getStartMoney().subtract(toExchange).compareTo(BigDecimal.ZERO) <= 0 ) {
                throw new RuntimeException("Not enough money");
            }
            newMoney = accountFrom.getStartMoney().subtract(toExchange);
            newMoneyTo = accountTo.getStartMoney().add(toExchange);
            accountFrom.setStartMoney(newMoney);
            accountTo.setStartMoney(newMoneyTo);
            subAccountRepository.saveAll(Arrays.asList(accountTo,accountFrom));
            subAccountRepository.flush();
        }
        return new ExchangeResponseDto()
                .setCurrencyFrom(exchange.getCurrencyFrom())
                .setCurrencyTo(exchange.getCurrencyTo())
                .setRate(nbpService.getCurrency().get(exchange.getCurrencyTo()).getBid())
                .setLeft(newMoney)
                .setStay(newMoneyTo);
    }

    @Transactional
    private User saveUser(RegisterUser user) {
        User savedUser = userRepository.saveAndFlush(mapper.map(user));
        Account account = accountRepository.saveAndFlush(mapper.map(savedUser, user.getMoney()));
        subAccountRepository.saveAll(mapper.generateSubAccounts(account));
        return savedUser;
    }
}
