package com.epam.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class TestOrderStatusHistoryService {
    @InjectMocks
    private OrderStatusHistoryServiceImpl orderStatusHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findByIdShouldThrowExceptionIfIdIsNull() {
        assertThatThrownBy(() ->
                orderStatusHistoryService.findById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Id must");
    }

    @Test
    void findByOrderIdShouldThrowExceptionIfOrderIdIsNull() {
        assertThatThrownBy(() ->
                orderStatusHistoryService.findAllByOrderId(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Order id must");
    }

    @Test
    void findByOrderStatusShouldThrowExceptionIfOrderStatusIsNull() {
        assertThatThrownBy(() ->
                orderStatusHistoryService.findAllByOrderStatus(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Order status must");
    }

    @Test
    void findByTimeAfterShouldThrowExceptionIfTimeAfterIsNull() {
        assertThatThrownBy(() ->
                orderStatusHistoryService.findAllByTimeAfter(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Time must");
    }

    @Test
    void findByTimeBeforeShouldThrowExceptionIfTimeBeforeIsNull() {
        assertThatThrownBy(() ->
                orderStatusHistoryService.findAllByTimeBefore(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Time must");
    }

    @Test
    void deleteByIdShouldThrowExceptionIfIdIsNull() {
        assertThatThrownBy(() ->
                orderStatusHistoryService.deleteById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Id must");
    }

}
