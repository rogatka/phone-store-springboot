package com.epam.store.dao;

import com.epam.store.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDAOImpl implements OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Order> findAll() {
        TypedQuery<Order> query = entityManager.createQuery("from Order", Order.class);
        return query.getResultList();
    }

    @Override
    public List<Order> findAllByAccountId(Long id) {
        TypedQuery<Order> query = entityManager.createQuery("select o from Order o where o.account.id=:id", Order.class)
                .setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public Optional<Order> findById(Long id) {
        List<Order> orders;
        Optional<Order> order;
        Query query = entityManager.createQuery("select o from Order o LEFT join fetch o.orderCards oc where o.id=:id")
                .setParameter("id", id);
        orders = query.getResultList();
        if (orders.isEmpty()) {
            order = Optional.empty();
        } else {
            order = Optional.of(orders.get(0));
        }
        return order;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        if (order.getId() == null) {
            entityManager.persist(order);
        } else {
            order = entityManager.merge(order);
        }
        return order;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Query query = entityManager.createQuery("delete from Order where id=:id")
                .setParameter("id", id);
        query.executeUpdate();
    }
}
