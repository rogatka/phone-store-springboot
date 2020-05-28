package com.epam.store.service;

import java.util.List;
import java.util.Optional;

public interface AbstractService<T, ID> {
    String ID_MUST_NOT_BE_NULL = "Id must not be null";

    List<T> findAll();
    Optional<T> findById(ID id);
    T save(T t);
    void deleteById(ID id);
}
