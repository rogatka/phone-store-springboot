package com.epam.store.service;

import com.epam.store.dao.AccountDAO;
import com.epam.store.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TestUserService {

    @Mock
    private AccountDAO accountDAO;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findByIdShouldThrowExceptionWhenIdIsNull() {
        assertThatThrownBy(()->
                userService.findById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Id must");
    }

    @Test
    public void saveShouldThrowExceptionWhenUserIsNull() {
        assertThatThrownBy(()->
                userService.save(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("User must");
    }

    @Test
    public void deleteByIdShouldThrowExceptionWhenIdIsNull() {
        assertThatThrownBy(()->
                userService.deleteById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Id must");
    }

    @Test
    public void deleteByIdShouldThrowExceptionIfAccountWithThatUserExists() {
        Account account = new Account();
        account.setId(1L);
        when (accountDAO.findByUserId(anyLong())).thenReturn(Optional.of(account));
        assertThatThrownBy(()->
                userService.deleteById(anyLong()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Cannot delete user");
    }

    @Test
    public void findAllByNameShouldThrowExceptionWhenNameIsNull() {
        assertThatThrownBy(()->
                userService.findAllByName(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("Name must");
    }
}
