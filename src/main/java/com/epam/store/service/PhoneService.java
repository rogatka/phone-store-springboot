package com.epam.store.service;

import com.epam.store.entity.Phone;

import java.util.List;
import java.util.Optional;

public interface PhoneService extends AbstractService<Phone,Long> {
    Optional<Phone> findByModelName(String modelName);
    void saveAll(List<Phone> phoneList);
}
