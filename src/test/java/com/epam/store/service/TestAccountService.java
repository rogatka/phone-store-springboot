package com.epam.store.service;

import com.epam.store.dao.OrderDAO;
import com.epam.store.entity.Order;
import com.epam.store.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TestAccountService {
    @Mock
    private OrderDAO orderDAO;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findByIdShouldThrowExceptionIfIdIsNull() {
        assertThatThrownBy(() ->
                accountService.findById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Id must");
    }

    @Test
    void findByUserIdShouldThrowExceptionIfUserIdIsNull() {
        assertThatThrownBy(() ->
                accountService.findByUserId(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("User id must");
    }

    @Test
    void deleteByIdShouldThrowExceptionIfIdIsNull() {
        assertThatThrownBy(() ->
                accountService.deleteById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Id must");
    }

    @Test
    void deleteByIdShouldThrowExceptionIfThereAreProcessingOrders() {
        Order order = new Order();
        order.setStatus(OrderStatus.PROCESSING);
        when(orderDAO.findAllByAccountId(anyLong())).thenReturn(Collections.singletonList(order));
        assertThatThrownBy(() ->
                accountService.deleteById(anyLong()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Cannot delete account");
    }

    @Test
    void saveShouldThrowExceptionIfAccountIsNull() {
        assertThatThrownBy(() ->
                accountService.save(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Account must");
    }
}
