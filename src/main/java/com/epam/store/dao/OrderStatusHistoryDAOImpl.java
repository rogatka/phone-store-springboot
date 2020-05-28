package com.epam.store.dao;

import com.epam.store.entity.OrderStatus;
import com.epam.store.entity.OrderStatusHistory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderStatusHistoryDAOImpl implements OrderStatusHistoryDAO {

    private EntityManagerFactory entityManagerFactory;

    public OrderStatusHistoryDAOImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<OrderStatusHistory> findAll() {
        List<OrderStatusHistory> orderStatusHistoryList;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("from OrderStatusHistory", OrderStatusHistory.class);
        orderStatusHistoryList = query.getResultList();
        entityManager.close();
        return orderStatusHistoryList;
    }

    @Override
    public Optional<OrderStatusHistory> findById(Long id) {
        OrderStatusHistory orderStatusHistory;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        orderStatusHistory = entityManager.find(OrderStatusHistory.class,id);
        entityManager.close();
        return Optional.ofNullable(orderStatusHistory);
    }

    @Override
    public List<OrderStatusHistory> findAllByOrderId(Long orderId) {
        List<OrderStatusHistory> orderStatusHistoryList;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("select osh from OrderStatusHistory osh where osh.order.id=:orderId", OrderStatusHistory.class)
                .setParameter("orderId",orderId);
        orderStatusHistoryList = query.getResultList();
        entityManager.close();
        return orderStatusHistoryList;
    }

    @Override
    public List<OrderStatusHistory> findAllByOrderStatus(OrderStatus orderStatus) {
        List<OrderStatusHistory> orderStatusHistoryList;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("select osh from OrderStatusHistory osh where osh.orderStatus=:orderStatus", OrderStatusHistory.class)
                .setParameter("orderStatus",orderStatus);
        orderStatusHistoryList = query.getResultList();
        entityManager.close();
        return orderStatusHistoryList;
    }

    @Override
    public List<OrderStatusHistory> findAllByTimeBefore(LocalDateTime beforeTime) {
        List<OrderStatusHistory> orderStatusHistoryList;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("select osh from OrderStatusHistory osh where osh.timeStamp < :beforeTime order by osh.timeStamp desc", OrderStatusHistory.class)
                .setParameter("beforeTime",beforeTime);
        orderStatusHistoryList = query.getResultList();
        entityManager.close();
        return orderStatusHistoryList;
    }

    @Override
    public List<OrderStatusHistory> findAllByTimeAfter(LocalDateTime afterTime) {
        List<OrderStatusHistory> orderStatusHistoryList;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<OrderStatusHistory> query = entityManager.createQuery("select osh from OrderStatusHistory osh where osh.timeStamp > :afterTime order by osh.timeStamp", OrderStatusHistory.class)
                .setParameter("afterTime",afterTime);
        orderStatusHistoryList = query.getResultList();
        entityManager.close();
        return orderStatusHistoryList;
    }

    @Override
    public OrderStatusHistory save(OrderStatusHistory orderStatusHistoryItem) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            if (orderStatusHistoryItem.getId() == null) {
                entityManager.persist(orderStatusHistoryItem);
            } else {
                orderStatusHistoryItem = entityManager.merge(orderStatusHistoryItem);
            }
            transaction.commit();
            return orderStatusHistoryItem;
        } catch (EntityExistsException |IllegalArgumentException e) {
            transaction.rollback();
            throw new IllegalArgumentException(e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Query query = entityManager.createQuery("delete from OrderStatusHistory where id=:id")
                .setParameter("id",id);
        try {
            query.executeUpdate();
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        } finally {
            entityManager.close();
        }
    }
}
