package com.epam.store.dao;

import com.epam.store.entity.Account;

import java.util.Optional;


public interface AccountDAO extends AbstractDAO<Account, Long>{
    Optional<Account> findByUserId(Long userId);
}
