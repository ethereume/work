package com.nfx.work.mapper;

import com.nfx.work.entity.SubAccount;
import com.nfx.work.frontdto.AccountDto;
import com.nfx.work.frontdto.RegisterUser;
import com.nfx.work.frontdto.RegisterUserDto;
import com.nfx.work.entity.Account;
import com.nfx.work.entity.User;
import com.nfx.work.frontdto.SubAccountDto;
import com.nfx.work.service.NbpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    private final NbpService nbpService;
    private final List<String> ibans =  Arrays.asList(
             "PL91203000151110000000993590",
             "PL86203000353110000000199160",
             "PL94160014921842613400000001",
             "PL67160014821842613400000002",
             "PL40160014721842613400000003",
             "PL13160014121842613400000004",
             "PL07160014721842613400000015",
             "PL56160014321242613400000006",
             "PL83160014621842613400000005",
             "PL77160514621842613400000016",
             "PL50165014621842613400000017",
             "PL05160001031842613400000008",
             "PL21160003031842613400000011",
             "PL64160002231842613400000013",
             "PL59160000331085511400000002",
             "PL16175010470000000011410243"
    );

    public User map(RegisterUser registerUser) {
        return new User()
                .setName(registerUser.getName())
                .setSurname(registerUser.getSurname())
                .setPesel(registerUser.getPesel());
    }

    public Account map(User user, BigDecimal money) {
        return new Account()
                .setUser(user)
                .setCurrency("PLN")
                .setIban(generateIban())
                .setStartMoney(money);
    }

    public List<SubAccount> generateSubAccounts(Account account) {
        return nbpService.getCurrency()
                .keySet()
                .stream()
                .map(it -> new SubAccount()
                        .setAccount(account)
                        .setStartMoney(BigDecimal.ZERO)
                        .setCurrency(it))
                .collect(Collectors.toList());
    }

    public RegisterUserDto map(User user,String message,boolean created) {
        return new RegisterUserDto()
                .setName(user.getName())
                .setSurname(user.getSurname())
                .setMessage(message)
                .setCreated(created);
    }

    public AccountDto map(Account account) {
        return new AccountDto()
                .setIban(account.getIban())
                .setMoney(account.getStartMoney())
                .setCurrency(account.getCurrency())
                .setUser(account.getUser().getName())
                .setSurname(account.getUser().getSurname())
                .setAccounts(account.getSubAccounts()
                        .stream()
                        .map(it -> new SubAccountDto()
                                .setCurrency(it.getCurrency())
                                .setMoney(it.getStartMoney())
                                .setMoney(it.getStartMoney()))
                        .sorted(Comparator.comparing(SubAccountDto::getCurrency))
                        .collect(Collectors.toList()));
    }

    private String generateIban() {
        Collections.shuffle(ibans);
        return ibans.get(0);
    }
}
