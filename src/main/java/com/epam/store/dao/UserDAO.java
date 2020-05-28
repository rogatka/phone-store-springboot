package com.epam.store.dao;

import com.epam.store.entity.Account;
import com.epam.store.entity.User;

import java.util.List;

public interface UserDAO extends AbstractDAO<User, Long>{
    List<User> findAllSortByFirstName();
    List<User> findAllSortByLastName();
    List<User> findAllByName(String name);
}
