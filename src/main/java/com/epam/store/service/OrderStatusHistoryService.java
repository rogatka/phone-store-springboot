package com.epam.store.service;

import com.epam.store.entity.OrderStatus;
import com.epam.store.entity.OrderStatusHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderStatusHistoryService extends AbstractService<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findAllByOrderId(Long orderId);
    List<OrderStatusHistory> findAllByOrderStatus(OrderStatus orderStatus);
    List<OrderStatusHistory> findAllByTimeBefore(LocalDateTime beforeTime);
    List<OrderStatusHistory> findAllByTimeAfter(LocalDateTime afterTime);
}
