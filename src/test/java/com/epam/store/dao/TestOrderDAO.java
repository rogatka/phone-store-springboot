package com.epam.store.dao;

import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.Account;
import com.epam.store.entity.Order;
import com.epam.store.entity.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = {TestOrderDAO.TestOrderDAOConfig.class})
@ExtendWith({SpringExtension.class})
@TestPropertySource("classpath:test-persistence.properties")
public class TestOrderDAO {

    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private AccountDAO accountDAO;


    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setup() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("create-tables.sql"), StandardCharsets.UTF_8));
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("populate-tables.sql"), StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() throws SQLException {
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("drop-tables.sql"), StandardCharsets.UTF_8));
    }

    @Test
    public void testFindAll() {
        List<Order> orders = orderDAO.findAll();
        assertEquals(5, orders.size());
        Order testOrder = new Order();
        Account account = accountDAO.findById(1L).get();
        testOrder.setAccount(account);
        orderDAO.save(testOrder);
        orders = orderDAO.findAll();
        assertEquals(6, orders.size());
    }

    @Test
    public void testFindAllByAccountId() {
        Account account = accountDAO.findById(1L).get();
        List<Order> orders = orderDAO.findAllByAccountId(1L);
        assertEquals(3, orders.size());
        Order order = new Order();
        order.setAccount(account);
        order.setStatus(OrderStatus.NOT_STARTED);
        orderDAO.save(order);
        orders = orderDAO.findAllByAccountId(1L);
        assertEquals(4,orders.size());
    }

    @Test
    public void testFindById() {
        assertFalse(orderDAO.findById(10L).isPresent());
        assertTrue(orderDAO.findById(3L).isPresent());
        OrderStatus status = OrderStatus.READY;
        assertEquals(status, orderDAO.findById(3L).get().getStatus());
    }

    @Test
    public void testSave() {
        Order testOrder = new Order();
        Account account = accountDAO.findById(1L).get();
        testOrder.setAccount(account);
        orderDAO.save(testOrder);
        assertTrue(orderDAO.findById(6L).isPresent());
    }

    @Test
    public void testDeleteById() {
        List<Order> orders = orderDAO.findAll();
        assertEquals(2, accountDAO.findAll().size());
        assertEquals(5, orders.size());
        orderDAO.deleteById(4L);
        orders = orderDAO.findAll();
        assertEquals(4, orders.size());
        assertEquals(2, accountDAO.findAll().size());
    }

    @Configuration
    @Import({TestJdbcConfig.class})
    static class TestOrderDAOConfig {
        @Bean
        public OrderDAO orderDAO(EntityManagerFactory entityManagerFactory) {
            return new OrderDAOImpl(entityManagerFactory);
        }

        @Bean
        public AccountDAO accountDAO(EntityManagerFactory entityManagerFactory) {
            return new AccountDAOImpl(entityManagerFactory);
        }
    }
}
