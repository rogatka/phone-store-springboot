package com.epam.store.dao;

import com.epam.store.Main;
import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.Account;
import com.epam.store.entity.Order;
import com.epam.store.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        Main.class,
        TestJdbcConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestOrderDAO {

    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private AccountDAO accountDAO;

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
}
