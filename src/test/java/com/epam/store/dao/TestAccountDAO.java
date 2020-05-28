package com.epam.store.dao;

import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.Account;
import com.epam.store.entity.User;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = {TestAccountDAO.TestAccountDAOConfig.class})
@ExtendWith({SpringExtension.class})
@TestPropertySource("classpath:test-persistence.properties")
public class TestAccountDAO {

    @Autowired
    private UserDAO userDAO;

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

    @Configuration
    @Import({TestJdbcConfig.class})
    static class TestAccountDAOConfig {
        @Bean
        public AccountDAO accountDAO(EntityManagerFactory entityManagerFactory) {
            return new AccountDAOImpl(entityManagerFactory);
        }

        @Bean
        public UserDAO userDAO(EntityManagerFactory entityManagerFactory) {
            return new UserDAOImpl(entityManagerFactory);
        }
    }
}
