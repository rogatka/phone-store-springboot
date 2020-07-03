package com.epam.store.service;

import com.epam.store.dao.AccountDAO;
import com.epam.store.dao.UserDAO;
import com.epam.store.entity.Account;
import com.epam.store.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {

    private static final String NAME_MUST_NOT_BE_NULL = "Name must not be null";
    private static final String USER_MUST_NOT_BE_NULL = "User must not be null";
    private UserDAO userDAO;
    private AccountDAO accountDAO;
    @Autowired
    public UserServiceImpl(UserDAO userDAO, AccountDAO accountDAO) {
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        Objects.requireNonNull(id, ID_MUST_NOT_BE_NULL);
        return userDAO.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {
        Objects.requireNonNull(user, USER_MUST_NOT_BE_NULL);
        return userDAO.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Objects.requireNonNull(id, ID_MUST_NOT_BE_NULL);
        Optional<Account> account = accountDAO.findByUserId(id);
        if (account.isPresent()) {
            throw new IllegalArgumentException(String.format("Cannot delete user with id=%d because there is account with id=%d which referenced to that user", id, account.get().getId()));
        }
        userDAO.deleteById(id);
    }

    @Override
    public List<User> findAllSortByFirstName() {
        return userDAO.findAllSortByFirstName();
    }

    @Override
    public List<User> findAllSortByLastName() {
        return userDAO.findAllSortByLastName();
    }

    @Override
    public List<User> findAllByName(String name) {
        Objects.requireNonNull(name, NAME_MUST_NOT_BE_NULL);
        return userDAO.findAllByName(name);
    }
}
