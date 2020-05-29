package com.epam.store.dao;

import com.epam.store.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery("from User", User.class);
        return query.getResultList();
    }

    @Override
    public List<User> findAllSortByFirstName() {
        TypedQuery<User> query = entityManager.createQuery("from User order by firstName, lastName, id", User.class);
        return query.getResultList();
    }

    @Override
    public List<User> findAllSortByLastName() {
        TypedQuery<User> query = entityManager.createQuery("from User order by lastName, firstName, id", User.class);
        return query.getResultList();
    }

    @Override
    public List<User> findAllByName(String name) {
        TypedQuery<User> query = entityManager.createQuery("from User where firstName like :name OR lastName like :name order by firstName, lastName, id", User.class)
                .setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            user = entityManager.merge(user);
        }
        return user;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Query query = entityManager.createQuery("delete from User where id=:id")
                .setParameter("id", id);
        query.executeUpdate();
    }
}
