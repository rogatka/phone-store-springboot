package com.epam.store.dao;

import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.*;
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


@ContextConfiguration(classes = {TestOrderCardDAO.TestOrderCardDAOConfig.class})
@ExtendWith({SpringExtension.class})
@TestPropertySource("classpath:test-persistence.properties")
public class TestOrderCardDAO {

    @Autowired
    private OrderCardDAO orderCardDAO;
    @Autowired
    private PhoneDAO phoneDAO;
    @Autowired
    private OrderDAO orderDAO;

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
        List<OrderCard> orderCards = orderCardDAO.findAll();
        assertEquals(9, orderCards.size());
        OrderCard testOrderCard = new OrderCard();
        Phone phone = phoneDAO.findById(1L).get();
        Order order = orderDAO.findById(1L).get();
        testOrderCard.setOrder(order);
        testOrderCard.setPhone(phone);
        testOrderCard.setItemCount(2L);
        orderCardDAO.save(testOrderCard);
        orderCards = orderCardDAO.findAll();
        assertEquals(10, orderCards.size());
    }

    @Test
    public void testFindAllSortByItemCountDesc() {
        List<OrderCard> orderCards = orderCardDAO.findAllSortByItemCountDesc();
        assertEquals(9, orderCards.size());
        assertEquals(6, orderCards.get(0).getItemCount());
    }

    @Test
    public void testFindAllByOrderId() {
        Order order = orderDAO.findById(2L).get();
        Phone phone = phoneDAO.findById(1L).get();
        List<OrderCard> orderCards = orderCardDAO.findAllByOrderId(2L);
        assertEquals(2, orderCards.size());
        OrderCard orderCard = new OrderCard();
        orderCard.setOrder(order);
        orderCard.setPhone(phone);
        orderCard.setItemCount(1L);
        orderCardDAO.save(orderCard);
        orderCards = orderCardDAO.findAllByOrderId(2L);
        assertEquals(3,orderCards.size());
    }

    @Test
    public void testFindAllByPhoneId() {
        Phone phone = phoneDAO.findById(2L).get();
        Order order = orderDAO.findById(2L).get();
        List<OrderCard> orderCards = orderCardDAO.findAllByPhoneId(2L);
        assertEquals(4, orderCards.size());
        OrderCard orderCard = new OrderCard();
        orderCard.setOrder(order);
        orderCard.setPhone(phone);
        orderCard.setItemCount(1L);
        orderCardDAO.save(orderCard);
        orderCards = orderCardDAO.findAllByPhoneId(2L);
        assertEquals(5,orderCards.size());
    }

    @Test
    public void testFindById() {
        assertFalse(orderCardDAO.findById(10L).isPresent());
        assertTrue(orderCardDAO.findById(6L).isPresent());
        OrderStatus status = OrderStatus.READY;
        assertEquals(5, orderCardDAO.findById(6L).get().getItemCount());
    }

    @Test
    public void testSave() {
        OrderCard testOrderCard = new OrderCard();
        Order order = orderDAO.findById(2L).get();
        Phone phone = phoneDAO.findById(1L).get();
        testOrderCard.setPhone(phone);
        testOrderCard.setOrder(order);
        testOrderCard.setItemCount(10L);
        orderCardDAO.save(testOrderCard);
        assertTrue(orderCardDAO.findById(10L).isPresent());
    }

    @Test
    public void testDeleteById() {
        List<OrderCard> orderCards = orderCardDAO.findAll();
        assertEquals(9, orderCards.size());
        orderCardDAO.deleteById(4L);
        orderCards = orderCardDAO.findAll();
        assertEquals(8, orderCards.size());
    }

    @Configuration
    @Import({TestJdbcConfig.class})
    static class TestOrderCardDAOConfig {
        @Bean
        public OrderCardDAO orderCardDAO(EntityManagerFactory entityManagerFactory) {
            return new OrderCardDAOImpl(entityManagerFactory);
        }

        @Bean
        public OrderDAO orderDAO(EntityManagerFactory entityManagerFactory) {
            return new OrderDAOImpl(entityManagerFactory);
        }

        @Bean
        public PhoneDAO phoneDAO(EntityManagerFactory entityManagerFactory) {
            return new PhoneDAOImpl(entityManagerFactory);
        }
    }
}
