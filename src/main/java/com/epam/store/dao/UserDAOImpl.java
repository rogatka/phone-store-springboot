package com.epam.store.dao;

import com.epam.store.entity.User;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private EntityManagerFactory entityManagerFactory;

    public UserDAOImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<User> findAll() {
        List<User> users;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<User> query = entityManager.createQuery("from User", User.class);
        users = query.getResultList();
        entityManager.close();
        return users;
    }

    @Override
    public List<User> findAllSortByFirstName() {
        List<User> users;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<User> query = entityManager.createQuery("from User order by firstName, lastName, id", User.class);
        users = query.getResultList();
        entityManager.close();
        return users;
    }

    @Override
    public List<User> findAllSortByLastName() {
        List<User> users;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<User> query = entityManager.createQuery("from User order by lastName, firstName, id", User.class);
        users = query.getResultList();
        entityManager.close();
        return users;
    }

    @Override
    public List<User> findAllByName(String name) {
        List<User> users;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<User> query = entityManager.createQuery("from User where firstName like :name OR lastName like :name order by firstName, lastName, id", User.class)
                .setParameter("name", "%" + name + "%");
        users = query.getResultList();
        entityManager.close();
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        User user;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        user = entityManager.find(User.class,id);
        entityManager.close();
        return Optional.ofNullable(user);
    }

    @Override
    public User save(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            if (user.getId() == null) {
                entityManager.persist(user);
            } else {
                user = entityManager.merge(user);
            }
            transaction.commit();
            return user;
        } catch (EntityExistsException|IllegalArgumentException e) {
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
        Query query = entityManager.createQuery("delete from User where id=:id")
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
