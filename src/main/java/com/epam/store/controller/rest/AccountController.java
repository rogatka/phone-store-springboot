package com.epam.store.controller.rest;

import com.epam.store.entity.Account;
import com.epam.store.service.AccountService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "accounts")
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<Account>> findAll() {
        return new ResponseEntity<>(accountService.findAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findById(@PathVariable(name = "id") Long accountId) throws NotFoundException {
        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isPresent()) {
            return new ResponseEntity<>(accountOptional.get(), HttpStatus.OK);
        } else {
            throw new NotFoundException("account with user id = " + accountId + " not exist");
        }
    }

    @GetMapping(path = "/user/{userId}")
    public ResponseEntity<Account> findAccountByUserId(@PathVariable(name = "userId") Long userId) throws NotFoundException {
        Optional<Account> account = accountService.findByUserId(userId);
        if (account.isPresent()) {
            return new ResponseEntity<>(account.get(), HttpStatus.OK);
        } else {
            throw new NotFoundException("account with user id = " + userId + " not exist");
        }
    }

    @PostMapping
    public void createAccount(@RequestBody Account account) {
        accountService.save(account);
    }

    @PutMapping("/{id}")
    public void updateAccount(@RequestBody Account account, @PathVariable Long id) throws NotFoundException {
        if (accountService.findById(id).isPresent()) {
            accountService.save(account);
        } else {
            throw new NotFoundException("account with id = " + account.getId() + " not exist");
        }
    }

    @DeleteMapping(path = "/{id}")
    public void deleteAccount(@PathVariable(name = "id") Long accountId) throws NotFoundException {
        if (accountService.findById(accountId).isPresent()) {
            accountService.deleteById(accountId);
        } else {
            throw new NotFoundException("account with id = " + accountId + " not exist");
        }
    }
}
