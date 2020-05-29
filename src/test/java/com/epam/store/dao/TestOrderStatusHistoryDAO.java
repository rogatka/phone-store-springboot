package com.epam.store.dao;

import com.epam.store.Main;
import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.Order;
import com.epam.store.entity.OrderStatus;
import com.epam.store.entity.OrderStatusHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        Main.class,
        TestJdbcConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestOrderStatusHistoryDAO {

    @Autowired
    private OrderStatusHistoryDAO orderStatusHistoryDAO;
    @Autowired
    private OrderDAO orderDAO;

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
}
