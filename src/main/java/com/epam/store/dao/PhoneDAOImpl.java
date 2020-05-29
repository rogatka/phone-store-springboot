package com.epam.store.dao;


import com.epam.store.entity.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class PhoneDAOImpl implements PhoneDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Phone> findAll() {
        TypedQuery<Phone> query = entityManager.createQuery("from Phone", Phone.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void saveAll(Collection<Phone> phoneList) {
        for (Phone phone : phoneList) {
            entityManager.merge(phone);
        }
    }

    @Override
    public Optional<Phone> findByModelName(String modelName) {
        TypedQuery<Phone> query = entityManager.createQuery("from Phone where model like :modelName", Phone.class)
                .setParameter("modelName", modelName.trim());
        List<Phone> phones = query.getResultList();
        if (phones.isEmpty()) {
            return Optional.empty();
        } else if (phones.size() > 1) {
            throw new RuntimeException("More than 1 phones with same model was found. Founded phones=" + phones.size() + ".Model name=" + modelName);
        }
        return Optional.of(phones.get(0));
    }

    @Override
    public Optional<Phone> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Phone.class, id));
    }

    @Override
    @Transactional
    public Phone save(Phone phone) {
        if (phone.getId() == null) {
            entityManager.persist(phone);
        } else {
            phone = entityManager.merge(phone);
        }
        return phone;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Query query = entityManager.createQuery("delete from Phone where id=:id")
                .setParameter("id", id);
        query.executeUpdate();
    }
}
