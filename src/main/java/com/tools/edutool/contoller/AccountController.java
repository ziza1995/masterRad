package com.tools.edutool.contoller;

import com.tools.edutool.dto.*;
import com.tools.edutool.exceptions.EduToolException;
import com.tools.edutool.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;


import javax.mail.AuthenticationFailedException;
import javax.servlet.http.HttpServletRequest;

import java.util.Collection;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping()
    public ResponseEntity<AccountDto> updateAccount(@RequestBody AccountDto  accountDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        User user = (User) authentication.getPrincipal();
        if(user != null && !user.getUsername().equals(accountDto.getUsername())){
            return new ResponseEntity<>(UNAUTHORIZED);
        }
        if(!authorities.contains(new SimpleGrantedAuthority("TEST"))){
            return new ResponseEntity<>(FORBIDDEN);
        }
        AccountDto account = accountService.update(accountDto);
        return new ResponseEntity<>(account, OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable String  username) {
        AccountDto account = accountService.getAccountById(username);
        return new ResponseEntity<>(account, OK);
    }

}
