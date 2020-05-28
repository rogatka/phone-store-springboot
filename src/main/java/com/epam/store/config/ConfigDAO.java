package com.epam.store.config;

import com.epam.store.dao.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Configuration
@Import({ConfigJdbc.class})
public class ConfigDAO {

    @Bean
    public UserDAO userDAO(EntityManagerFactory entityManagerFactory) {
        return new UserDAOImpl(entityManagerFactory);
    }

    @Bean
    public AccountDAO accountDAO(EntityManagerFactory entityManagerFactory) {
        return new AccountDAOImpl(entityManagerFactory);
    }

    @Bean
    public OrderDAO orderDAO(EntityManagerFactory entityManagerFactory) {
        return new OrderDAOImpl(entityManagerFactory);
    }

    @Bean
    public PhoneDAO phoneDAO(EntityManagerFactory entityManagerFactory) {
        return new PhoneDAOImpl(entityManagerFactory);
    }

    @Bean
    public OrderCardDAO orderCardDAO(EntityManagerFactory entityManagerFactory) {
        return new OrderCardDAOImpl(entityManagerFactory);
    }

    @Bean
    public OrderStatusHistoryDAO orderStatusHistoryDAO(EntityManagerFactory entityManagerFactory) {
        return new OrderStatusHistoryDAOImpl(entityManagerFactory);
    }
}
