package com.epam.store.dao;

import java.util.List;
import java.util.Optional;

public interface AbstractDAO<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    T save(T t);
    void deleteById(ID id);
}
