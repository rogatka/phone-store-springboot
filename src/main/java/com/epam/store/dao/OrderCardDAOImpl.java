package com.epam.store.dao;

import com.epam.store.entity.OrderCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderCardDAOImpl implements OrderCardDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OrderCard> findAll() {
        TypedQuery<OrderCard> query = entityManager.createQuery("from OrderCard", OrderCard.class);
        return query.getResultList();
    }

    @Override
    public Optional<OrderCard> findById(Long id) {
        return Optional.ofNullable(entityManager.find(OrderCard.class, id));
    }

    @Override
    public List<OrderCard> findAllByOrderId(Long id) {
        TypedQuery<OrderCard> query = entityManager.createQuery("select oc from OrderCard oc where oc.order.id=:id", OrderCard.class)
                .setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public List<OrderCard> findAllByPhoneId(Long id) {
        TypedQuery<OrderCard> query = entityManager.createQuery("select oc from OrderCard oc where oc.phone.id=:id", OrderCard.class)
                .setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public List<OrderCard> findAllSortByItemCountDesc() {
        TypedQuery<OrderCard> query = entityManager.createQuery("from OrderCard order by itemCount desc", OrderCard.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public OrderCard save(OrderCard orderCard) {
        if (orderCard.getId() == null) {
            entityManager.persist(orderCard);
        } else {
            orderCard = entityManager.merge(orderCard);
        }
        return orderCard;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Query query = entityManager.createQuery("delete from OrderCard where id=:id")
                .setParameter("id", id);
        query.executeUpdate();
    }
}
