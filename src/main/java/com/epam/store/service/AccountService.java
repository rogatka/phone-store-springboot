package com.epam.store.service;

import com.epam.store.entity.Account;

import java.util.Optional;

public interface AccountService extends AbstractService<Account, Long> {
    Optional<Account> findByUserId(Long userId);
}
