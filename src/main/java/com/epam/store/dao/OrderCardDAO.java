package com.epam.store.dao;

import com.epam.store.entity.OrderCard;

import java.math.BigDecimal;
import java.util.List;

public interface OrderCardDAO extends AbstractDAO<OrderCard, Long> {
    List<OrderCard> findAllSortByItemCountDesc();
    List<OrderCard> findAllByOrderId(Long id);
    List<OrderCard> findAllByPhoneId(Long id);
}
