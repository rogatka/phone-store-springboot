package com.epam.store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class TestJdbcConfig {
    private Environment environment;

    public TestJdbcConfig(Environment environment) {
        this.environment = environment;
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
        dataSource.setDriverClassName(environment.getProperty("jdbc.driver"));
        dataSource.setUrl(environment.getProperty("jdbc.url"));
        dataSource.setUsername(environment.getProperty("jdbc.user"));
        dataSource.setPassword(environment.getProperty("jdbc.password"));
        return dataSource;
    }

    private Properties getJdbcHibernateProperties() {
        Properties props = new Properties();
        props.put(org.hibernate.cfg.Environment.DRIVER, environment.getProperty("jdbc.driver"));
        props.put(org.hibernate.cfg.Environment.URL, environment.getProperty("jdbc.url"));
        props.put(org.hibernate.cfg.Environment.USER, environment.getProperty("jdbc.user"));
        props.put(org.hibernate.cfg.Environment.PASS, environment.getProperty("jdbc.password"));
        props.put(org.hibernate.cfg.Environment.DIALECT, environment.getProperty("hibernate.dialect"));
        props.put(org.hibernate.cfg.Environment.SHOW_SQL, environment.getProperty("hibernate.show_sql"));
        return props;
    }
}
