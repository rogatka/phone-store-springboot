package com.epam.store.dao;

import com.epam.store.Main;
import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.Account;
import com.epam.store.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        Main.class,
        TestJdbcConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestAccountDAO {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Test
    public void testFindAll() {
        List<Account> accounts = accountDAO.findAll();
        assertEquals(2, accounts.size());
        Account testAccount = new Account();
        BigDecimal amount = BigDecimal.valueOf(12333.314666).setScale(2, BigDecimal.ROUND_HALF_UP);
        testAccount.setAmount(amount);
        User user = userDAO.findById(3L).get();
        user.setAccount(testAccount);
        userDAO.save(user);
        accounts = accountDAO.findAll();
        assertEquals(3, accounts.size());
        assertEquals(amount, accounts.get(2).getAmount());
    }

    @Test
    public void testFindById() {
        assertFalse(accountDAO.findById(10L).isPresent());
        assertTrue(accountDAO.findById(1L).isPresent());
        BigDecimal amount = BigDecimal.valueOf(24500.567).setScale(2, BigDecimal.ROUND_HALF_UP);
        assertEquals(amount, accountDAO.findById(1L).get().getAmount());
    }

    @Test
    public void testSave() {
        Account testAccount = new Account();
        testAccount.setAmount(BigDecimal.valueOf(76621.2148).setScale(2, BigDecimal.ROUND_HALF_UP));
        User user = new User();
        user.setFirstName("James");
        user.setLastName("Franco");
        user = userDAO.save(user);
        testAccount.setUser(user);
        testAccount = accountDAO.save(testAccount);
        assertEquals(BigDecimal.valueOf(76621.21), accountDAO.findById(3L).get().getAmount());
    }

    @Test
    public void testDeleteById() {
        List<Account> accounts = accountDAO.findAll();
        assertEquals(2, accounts.size());
        accountDAO.deleteById(2L);
        accounts = accountDAO.findAll();
        assertEquals(1, accounts.size());
        assertEquals(BigDecimal.valueOf(24500.57), accountDAO.findById(1L).get().getAmount());
    }
}
