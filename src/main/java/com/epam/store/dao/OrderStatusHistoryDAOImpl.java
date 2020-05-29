package com.epam.store.dao;

import com.epam.store.entity.OrderStatus;
import com.epam.store.entity.OrderStatusHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderStatusHistoryDAOImpl implements OrderStatusHistoryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OrderStatusHistory> findAll() {
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("from OrderStatusHistory", OrderStatusHistory.class);
        return query.getResultList();
    }

    @Override
    public Optional<OrderStatusHistory> findById(Long id) {
        return Optional.ofNullable(entityManager.find(OrderStatusHistory.class, id));
    }

    @Override
    public List<OrderStatusHistory> findAllByOrderId(Long orderId) {
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("select osh from OrderStatusHistory osh where osh.order.id=:orderId", OrderStatusHistory.class)
                .setParameter("orderId", orderId);
        return query.getResultList();
    }

    @Override
    public List<OrderStatusHistory> findAllByOrderStatus(OrderStatus orderStatus) {
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("select osh from OrderStatusHistory osh where osh.orderStatus=:orderStatus", OrderStatusHistory.class)
                .setParameter("orderStatus", orderStatus);
        return query.getResultList();
    }

    @Override
    public List<OrderStatusHistory> findAllByTimeBefore(LocalDateTime beforeTime) {
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("select osh from OrderStatusHistory osh where osh.timeStamp < :beforeTime order by osh.timeStamp desc", OrderStatusHistory.class)
                .setParameter("beforeTime", beforeTime);
        return query.getResultList();
    }

    @Override
    public List<OrderStatusHistory> findAllByTimeAfter(LocalDateTime afterTime) {
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("select osh from OrderStatusHistory osh where osh.timeStamp > :afterTime order by osh.timeStamp", OrderStatusHistory.class)
                .setParameter("afterTime", afterTime);
        return query.getResultList();
    }

    @Override
    @Transactional
    public OrderStatusHistory save(OrderStatusHistory orderStatusHistoryItem) {
        if (orderStatusHistoryItem.getId() == null) {
            entityManager.persist(orderStatusHistoryItem);
        } else {
            orderStatusHistoryItem = entityManager.merge(orderStatusHistoryItem);
        }
        return orderStatusHistoryItem;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Query query = entityManager.createQuery("delete from OrderStatusHistory where id=:id")
                .setParameter("id", id);
        query.executeUpdate();
    }
}
