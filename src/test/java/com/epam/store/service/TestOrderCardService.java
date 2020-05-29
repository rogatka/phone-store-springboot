package com.epam.store.service;

import com.epam.store.dao.OrderCardDAO;
import com.epam.store.dao.OrderDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class TestOrderCardService {

    @Mock
    private OrderCardDAO orderCardDAO;
    @Mock
    private OrderDAO orderDAO;

    @InjectMocks
    private OrderCardServiceImpl orderCardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findByIdShouldThrowExceptionIfIdIsNull() {
        assertThatThrownBy(() ->
                orderCardService.findById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Id must");
    }

    @Test
    void findByPhoneIdShouldThrowExceptionIfPhoneIdIsNull() {
        assertThatThrownBy(() ->
                orderCardService.findAllByPhoneId(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Phone id must");
    }

    @Test
    void findByOrderIdShouldThrowExceptionIfOrderIdIsNull() {
        assertThatThrownBy(() ->
                orderCardService.findAllByOrderId(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Order id must");
    }

    @Test
    void deleteByIdShouldThrowExceptionIfIdIsNull() {
        assertThatThrownBy(() ->
                orderCardService.deleteById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Id must");
    }

    @Test
    void saveShouldThrowExceptionIfOrderCardIsNull() {
        assertThatThrownBy(() ->
                orderCardService.save(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Order card must");
    }
}
