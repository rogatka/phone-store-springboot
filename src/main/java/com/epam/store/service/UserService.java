package com.epam.store.service;

import com.epam.store.entity.User;

import java.util.List;

public interface UserService extends AbstractService<User, Long>{
    List<User> findAllSortByFirstName();
    List<User> findAllSortByLastName();
    List<User> findAllByName(String name);
}
