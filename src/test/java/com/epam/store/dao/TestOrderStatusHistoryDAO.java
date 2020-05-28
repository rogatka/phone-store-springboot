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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = {TestOrderStatusHistoryDAO.TestOrderStatusHistoryDAOConfig.class})
@ExtendWith({SpringExtension.class})
@TestPropertySource("classpath:test-persistence.properties")
public class TestOrderStatusHistoryDAO {

    @Autowired
    private OrderStatusHistoryDAO orderStatusHistoryDAO;
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
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryDAO.findAll();
        assertEquals(10, orderStatusHistoryList.size());
        OrderStatusHistory testOrderStatusHistory = new OrderStatusHistory();
        Order order = orderDAO.findById(4L).get();
        testOrderStatusHistory.setOrder(order);
        testOrderStatusHistory.setOrderStatus(OrderStatus.PROCESSING);
        testOrderStatusHistory.setTimeStamp(LocalDateTime.now());
        orderStatusHistoryDAO.save(testOrderStatusHistory);
        orderStatusHistoryList = orderStatusHistoryDAO.findAll();
        assertEquals(11, orderStatusHistoryList.size());
    }

    @Test
    public void testFindAllByOrderId() {
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryDAO.findAllByOrderId(4L);
        assertEquals(1, orderStatusHistoryList.size());
        OrderStatusHistory testOrderStatusHistory = new OrderStatusHistory();
        Order order = orderDAO.findById(4L).get();
        testOrderStatusHistory.setOrder(order);
        testOrderStatusHistory.setOrderStatus(OrderStatus.PROCESSING);
        testOrderStatusHistory.setTimeStamp(LocalDateTime.now());
        orderStatusHistoryDAO.save(testOrderStatusHistory);
        orderStatusHistoryList = orderStatusHistoryDAO.findAllByOrderId(4L);
        assertEquals(2,orderStatusHistoryList.size());
    }

    @Test
    public void testFindAllByOrderStatus() {
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryDAO.findAllByOrderStatus(OrderStatus.NOT_STARTED);
        assertEquals(5, orderStatusHistoryList.size());
        OrderStatusHistory testOrderStatusHistory = new OrderStatusHistory();
        Order order = orderDAO.findById(4L).get();
        testOrderStatusHistory.setOrder(order);
        testOrderStatusHistory.setOrderStatus(OrderStatus.NOT_STARTED);
        testOrderStatusHistory.setTimeStamp(LocalDateTime.now());
        orderStatusHistoryDAO.save(testOrderStatusHistory);
        orderStatusHistoryList = orderStatusHistoryDAO.findAllByOrderStatus(OrderStatus.NOT_STARTED);
        assertEquals(6,orderStatusHistoryList.size());
    }

    @Test
    public void testFindAllByTimeBeforeAndAfter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.of(2020, 5, 20, 0, 0);
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryDAO.findAllByTimeBefore(date);
        assertEquals(2, orderStatusHistoryList.size());
        orderStatusHistoryList = orderStatusHistoryDAO.findAllByTimeAfter(date);
        assertEquals(8, orderStatusHistoryList.size());
    }

    @Test
    public void testFindById() {
        assertFalse(orderStatusHistoryDAO.findById(11L).isPresent());
        assertTrue(orderStatusHistoryDAO.findById(6L).isPresent());
        assertEquals(OrderStatus.READY, orderStatusHistoryDAO.findById(6L).get().getOrderStatus());
    }

    @Test
    public void testSave() {
        assertFalse(orderStatusHistoryDAO.findById(11L).isPresent());
        OrderStatusHistory testOrderStatusHistory = new OrderStatusHistory();
        Order order = orderDAO.findById(4L).get();
        testOrderStatusHistory.setOrder(order);
        testOrderStatusHistory.setOrderStatus(OrderStatus.NOT_STARTED);
        testOrderStatusHistory.setTimeStamp(LocalDateTime.now());
        orderStatusHistoryDAO.save(testOrderStatusHistory);
        assertTrue(orderStatusHistoryDAO.findById(11L).isPresent());
    }

    @Test
    public void testDeleteById() {
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryDAO.findAll();
        assertEquals(10, orderStatusHistoryList.size());
        orderStatusHistoryDAO.deleteById(4L);
        orderStatusHistoryList = orderStatusHistoryDAO.findAll();
        assertEquals(9, orderStatusHistoryList.size());
    }

    @Configuration
    @Import({TestJdbcConfig.class})
    static class TestOrderStatusHistoryDAOConfig {
        @Bean
        public OrderStatusHistoryDAO orderStatusHistoryDAO(EntityManagerFactory entityManagerFactory) {
            return new OrderStatusHistoryDAOImpl(entityManagerFactory);
        }

        @Bean
        public OrderDAO orderDAO(EntityManagerFactory entityManagerFactory) {
            return new OrderDAOImpl(entityManagerFactory);
        }
    }
}
