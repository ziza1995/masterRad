package com.tools.edutool.service;

import com.tools.edutool.EdutoolApplication;
import com.tools.edutool.dto.AccountDto;
import com.tools.edutool.exceptions.EduToolException;
import com.tools.edutool.model.Account;
import com.tools.edutool.model.User;
import com.tools.edutool.repository.AccountRepository;
import com.tools.edutool.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AccountDto getAccountById(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new EduToolException("No user " +
                "Found with username : " + username));
        Optional<Account> accountOptional =  accountRepository.findByUser(user);
        Account account = accountOptional.orElseGet(()-> {
            Account newAccount = new Account();
            newAccount.setUser(user);
            accountRepository.save(newAccount);
            return  newAccount;
        });
        AccountDto accountDto = mapDataToUserDetailsDto(user, account);

        return  accountDto;

    }

    private AccountDto mapDataToUserDetailsDto(User user, Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountNumber(account.getAccountNumber());
        accountDto.setUsername(account.getUser().getUsername());

        return accountDto;
    }
}
