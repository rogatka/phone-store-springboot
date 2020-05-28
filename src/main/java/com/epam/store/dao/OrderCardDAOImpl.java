package com.epam.store.dao;

import com.epam.store.entity.OrderCard;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

public class OrderCardDAOImpl implements OrderCardDAO {

    private EntityManagerFactory entityManagerFactory;

    public OrderCardDAOImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<OrderCard> findAll() {
        List<OrderCard> orderCards;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<OrderCard> query = entityManager.createQuery("from OrderCard", OrderCard.class);
        orderCards = query.getResultList();
        entityManager.close();
        return orderCards;
    }

    @Override
    public Optional<OrderCard> findById(Long id) {
        OrderCard orderCard;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        orderCard = entityManager.find(OrderCard.class, id);
        entityManager.close();
        return Optional.ofNullable(orderCard);
    }

    @Override
    public List<OrderCard> findAllByOrderId(Long id) {
        List<OrderCard> orderCards;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<OrderCard> query = entityManager.createQuery("select oc from OrderCard oc where oc.order.id=:id", OrderCard.class)
                .setParameter("id",id);
        orderCards = query.getResultList();
        entityManager.close();
        return orderCards;
    }

    @Override
    public List<OrderCard> findAllByPhoneId(Long id) {
        List<OrderCard> orderCards;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<OrderCard> query = entityManager.createQuery("select oc from OrderCard oc where oc.phone.id=:id", OrderCard.class)
                .setParameter("id",id);
        orderCards = query.getResultList();
        entityManager.close();
        return orderCards;
    }

    @Override
    public List<OrderCard> findAllSortByItemCountDesc() {
        List<OrderCard> orderCards;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<OrderCard> query = entityManager.createQuery("from OrderCard order by itemCount desc", OrderCard.class);
        orderCards = query.getResultList();
        entityManager.close();
        return orderCards;
    }

    @Override
    public OrderCard save(OrderCard orderCard) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            if (orderCard.getId() == null) {
                entityManager.persist(orderCard);
            } else {
                orderCard = entityManager.merge(orderCard);
            }
            transaction.commit();
            entityManager.close();
            return orderCard;
        } catch (EntityExistsException |IllegalArgumentException e) {
            transaction.rollback();
            entityManager.close();
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Query query = entityManager.createQuery("delete from OrderCard where id=:id")
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
