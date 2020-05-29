package com.epam.store.service;

import com.epam.store.dao.OrderCardDAO;
import com.epam.store.dao.PhoneDAO;
import com.epam.store.entity.OrderCard;
import com.epam.store.entity.OrderStatus;
import com.epam.store.entity.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
@Service
public class PhoneServiceImpl implements PhoneService {
    public static final String PHONE_MUST_NOT_BE_NULL = "Phone must not be null";
    public static final String MODEL_NAME_MUST_NOT_BE_NULL = "Model name must not be null";
    public static final String PHONE_LIST_MUST_NOT_BE_NULL = "Phone list must not be null";

    private PhoneDAO phoneDAO;
    private OrderCardDAO orderCardDAO;

    @Autowired
    public PhoneServiceImpl(PhoneDAO phoneDAO, OrderCardDAO orderCardDAO) {
        this.phoneDAO = phoneDAO;
        this.orderCardDAO = orderCardDAO;
    }

    @Override
    public Optional<Phone> findByModelName(String modelName) {
        Objects.requireNonNull(modelName, MODEL_NAME_MUST_NOT_BE_NULL);
        return phoneDAO.findByModelName(modelName);
    }

    @Override
    public List<Phone> findAll() {
        return phoneDAO.findAll();
    }

    @Override
    public Optional<Phone> findById(Long id) {
        Objects.requireNonNull(id, ID_MUST_NOT_BE_NULL);
        return phoneDAO.findById(id);
    }

    @Override
    @Transactional
    public Phone save(Phone phone) {
        Objects.requireNonNull(phone, PHONE_MUST_NOT_BE_NULL);
        return phoneDAO.save(phone);
    }

    @Override
    @Transactional
    public void saveAll(List<Phone> phoneList) {
        Objects.requireNonNull(phoneList, PHONE_LIST_MUST_NOT_BE_NULL);
        phoneDAO.saveAll(phoneList);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Objects.requireNonNull(id, ID_MUST_NOT_BE_NULL);
        List<OrderCard> orderCards = orderCardDAO.findAllByPhoneId(id);
        if (!orderCards.isEmpty()) {
            for (OrderCard orderCard : orderCards) {
                if (orderCard.getOrder().getStatus() == OrderStatus.PROCESSING) {
                    throw new IllegalArgumentException(String.format("Cannot delete phone with id=%d because there is processing order(id=%d) with that phone", id, orderCard.getOrder().getId()));
                }
            }
        }
        phoneDAO.deleteById(id);
    }
}
