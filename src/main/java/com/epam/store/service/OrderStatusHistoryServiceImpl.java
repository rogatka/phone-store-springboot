package com.epam.store.service;

import com.epam.store.dao.OrderStatusHistoryDAO;
import com.epam.store.entity.OrderStatus;
import com.epam.store.entity.OrderStatusHistory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class OrderStatusHistoryServiceImpl implements OrderStatusHistoryService {
    public static final String ORDER_ID_MUST_NOT_BE_NULL = "Order id must not be null";
    public static final String ORDER_STATUS_MUST_NOT_BE_NULL = "Order status must not be null";
    public static final String TIME_MUST_NOT_BE_NULL = "Time must not be null";
    public static final String ORDER_STATUS_HISTORY_ITEM_MUST_NOT_BE_NULL = "Order status history item must not be null";

    private OrderStatusHistoryDAO orderStatusHistoryDAO;

    public OrderStatusHistoryServiceImpl(OrderStatusHistoryDAO orderStatusHistoryDAO) {
        this.orderStatusHistoryDAO = orderStatusHistoryDAO;
    }

    @Override
    public List<OrderStatusHistory> findAllByOrderId(Long orderId) {
        Objects.requireNonNull(orderId, ORDER_ID_MUST_NOT_BE_NULL);
        return orderStatusHistoryDAO.findAllByOrderId(orderId);
    }

    @Override
    public List<OrderStatusHistory> findAllByOrderStatus(OrderStatus orderStatus) {
        Objects.requireNonNull(orderStatus, ORDER_STATUS_MUST_NOT_BE_NULL);
        return orderStatusHistoryDAO.findAllByOrderStatus(orderStatus);
    }

    @Override
    public List<OrderStatusHistory> findAllByTimeBefore(LocalDateTime beforeTime) {
        Objects.requireNonNull(beforeTime, TIME_MUST_NOT_BE_NULL);
        return orderStatusHistoryDAO.findAllByTimeBefore(beforeTime);
    }

    @Override
    public List<OrderStatusHistory> findAllByTimeAfter(LocalDateTime afterTime) {
        Objects.requireNonNull(afterTime, TIME_MUST_NOT_BE_NULL);
        return orderStatusHistoryDAO.findAllByTimeAfter(afterTime);
    }

    @Override
    public List<OrderStatusHistory> findAll() {
        return orderStatusHistoryDAO.findAll();
    }

    @Override
    public Optional<OrderStatusHistory> findById(Long id) {
        Objects.requireNonNull(id, ID_MUST_NOT_BE_NULL);
        return orderStatusHistoryDAO.findById(id);
    }

    @Override
    @Transactional
    public OrderStatusHistory save(OrderStatusHistory orderStatusHistory) {
        Objects.requireNonNull(orderStatusHistory, ORDER_STATUS_HISTORY_ITEM_MUST_NOT_BE_NULL);
        return orderStatusHistoryDAO.save(orderStatusHistory);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Objects.requireNonNull(id, ID_MUST_NOT_BE_NULL);
        orderStatusHistoryDAO.deleteById(id);
    }
}
