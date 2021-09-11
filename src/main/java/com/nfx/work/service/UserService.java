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
import java.util.*;
import java.util.function.Function;
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
        Set<String> currency= new HashSet<>(nbpService.getCurrency().keySet());
        currency.add("PLN");
        return new UserDictionary()
                .setCurrency(currency.stream().sorted().collect(Collectors.toList()))
                .setUsers(userRepository.findAll().stream().map(mapper::map).collect(Collectors.toList()));
    }

    @Transactional
    public ExchangeResponseDto exchange(ExchangeDto exchange) {
        User user = userRepository.findByPesel(exchange.getPesel()).orElseThrow(() -> new RuntimeException("User not exists in databases"));
        if(StringUtils.isBlank(exchange.getCurrencyFrom()) || StringUtils.isBlank(exchange.getCurrencyTo()) || Objects.isNull(exchange.getMoney())) {
            throw new RuntimeException("Parameter must be set");
        }
        if(exchange.getCurrencyFrom().equalsIgnoreCase(exchange.getCurrencyTo())) {
            throw new RuntimeException("You cannot do that");
        }
        Account account = user.getAccount();
        BigDecimal newMoney;
        BigDecimal newMoneyTo;
        BigDecimal rate;
        if(exchange.getCurrencyFrom().equals("PLN")) {
            SubAccount subAccount = user.getAccount().getSubAccounts()
                    .stream()
                    .filter(it -> exchange.getCurrencyTo().toUpperCase().equals(it.getCurrency()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("There is no account with this currency " + exchange.getCurrencyTo()));
            rate = nbpService.getCurrency().get(exchange.getCurrencyTo()).getBid();
            BigDecimal toExchange = exchange.getMoney().multiply(rate);
            if(account.getStartMoney().subtract(toExchange).compareTo(BigDecimal.ZERO) <= 0 ) {
                throw new RuntimeException("Not enough money");
            }
            newMoney = account.getStartMoney().subtract(toExchange);
            newMoneyTo = subAccount.getStartMoney().add(exchange.getMoney());
            accountRepository.saveAndFlush(account.setStartMoney(newMoney));
            subAccountRepository.saveAndFlush(subAccount.setStartMoney(newMoneyTo));
        } else {
            if(!exchange.getCurrencyTo().equals("PLN")) {
                throw new RuntimeException("No rate for currencyPair "+ exchange.getCurrencyFrom() + "/" + exchange.getCurrencyTo());
            }
            Map<String, SubAccount> subAccounts = account.getSubAccounts()
                    .stream()
                    .collect(Collectors.toMap((SubAccount::getCurrency), Function.identity()));
            SubAccount accountFrom = Optional.ofNullable(subAccounts.get(exchange.getCurrencyFrom().toUpperCase())).orElseThrow(() -> new RuntimeException("There is no account with this currency " + exchange.getCurrencyFrom()));
            if(accountFrom.getStartMoney().subtract(exchange.getMoney()).compareTo(BigDecimal.ZERO) <= 0 ) {
                throw new RuntimeException("Not enough money");
            }
            rate = nbpService.getCurrency().get(exchange.getCurrencyFrom()).getBid();
            BigDecimal toExchange = exchange.getMoney().multiply(rate);
            newMoneyTo = account.getStartMoney().add(toExchange);
            account.setStartMoney(newMoneyTo);
            newMoney = accountFrom.getStartMoney().subtract(exchange.getMoney());
            accountFrom.setStartMoney(newMoney);
            accountRepository.saveAndFlush(account);
            subAccountRepository.saveAndFlush(accountFrom);
        }
        return new ExchangeResponseDto()
                .setCurrencyFrom(exchange.getCurrencyFrom())
                .setCurrencyTo(exchange.getCurrencyTo())
                .setRate(rate)
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
