package com.epam.store.dao;

import com.epam.store.entity.Phone;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PhoneDAO extends AbstractDAO<Phone, Long> {
    Optional<Phone> findByModelName(String modelName);
    void saveAll(List<Phone> phoneList);
}
