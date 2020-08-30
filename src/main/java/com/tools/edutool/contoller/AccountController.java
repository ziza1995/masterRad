package com.tools.edutool.contoller;

import com.tools.edutool.dto.*;
import com.tools.edutool.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{username}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable String  username) {
        AccountDto account = accountService.getAccountById(username);
        return new ResponseEntity<>(account, OK);
    }

}
