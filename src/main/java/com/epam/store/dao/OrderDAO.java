package com.epam.store.dao;

import com.epam.store.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderDAO extends AbstractDAO<Order,Long>{
    List<Order> findAllByAccountId(Long id);
}
