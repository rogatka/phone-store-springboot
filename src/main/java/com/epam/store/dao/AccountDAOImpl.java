package com.epam.store.dao;

import com.epam.store.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountDAOImpl implements AccountDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Account> findAll() {
        TypedQuery<Account> query = entityManager.createQuery("select u from Account u", Account.class);
        return query.getResultList();
    }

    @Override
    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Account.class, id));
    }

    @Override
    public Optional<Account> findByUserId(Long userId) {
        TypedQuery<Account> query = entityManager.createQuery("from Account where user.id=:id", Account.class)
                .setParameter("id", userId);
        List<Account> accounts = query.getResultList();
        if (accounts.isEmpty()) {
            return Optional.empty();
        } else if (accounts.size() > 1) {
            throw new RuntimeException("More than 1 account with same user was found. Founded accounts=" + accounts.size() + ".User Id=" + userId);
        }
        return Optional.of(accounts.get(0));
    }

    @Override
    @Transactional
    public Account save(Account account) {
        if (account.getId() == null) {
            entityManager.persist(account);
        } else {
            account = entityManager.merge(account);
        }
        return account;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Query query = entityManager.createQuery("delete from Account where id=:id")
                .setParameter("id", id);
        query.executeUpdate();
    }
}
