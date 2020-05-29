package com.epam.store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@PropertySource("classpath:test-persistence.properties")
public class TestJdbcConfig {
    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() throws SQLException {
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("drop-tables.sql"), StandardCharsets.UTF_8));
        ScriptUtils.executeSqlScript(dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("create-tables.sql"), StandardCharsets.UTF_8));
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("populate-tables.sql"), StandardCharsets.UTF_8));
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan("com.epam.store.entity");
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setJpaDialect(new HibernateJpaDialect());
        entityManagerFactory.setJpaProperties(getJdbcHibernateProperties());
        return entityManagerFactory;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getProperty("jdbc.url"));
        dataSource.setUsername(environment.getProperty("jdbc.user"));
        dataSource.setPassword(environment.getProperty("jdbc.password"));
        return dataSource;
    }

    private Properties getJdbcHibernateProperties() {
        Properties props = new Properties();
        props.put(org.hibernate.cfg.Environment.DRIVER, environment.getProperty("jdbc.driverClassName"));
        props.put(org.hibernate.cfg.Environment.URL, environment.getProperty("jdbc.url"));
        props.put(org.hibernate.cfg.Environment.USER, environment.getProperty("jdbc.user"));
        props.put(org.hibernate.cfg.Environment.PASS, environment.getProperty("jdbc.password"));
        props.put(org.hibernate.cfg.Environment.DIALECT, environment.getProperty("hibernate.dialect"));
        props.put(org.hibernate.cfg.Environment.SHOW_SQL, environment.getProperty("hibernate.show_sql"));
        return props;
    }
}
