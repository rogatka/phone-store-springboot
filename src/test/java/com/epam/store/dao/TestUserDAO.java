package com.epam.store.dao;

import com.epam.store.config.TestJdbcConfig;
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
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = {TestUserDAO.TestUserDAOConfig.class})
@ExtendWith({SpringExtension.class})   // this is replace to @RunWith(SpringJunit4ClassRunner.class)
@TestPropertySource("classpath:test-persistence.properties")
public class TestUserDAO {

    @Autowired
    private UserDAO userDAO;

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
        List<User> userList = userDAO.findAll();
        assertEquals(3, userList.size());
        assertEquals("Michael", userList.get(0).getFirstName());
    }

    @Test
    public void testFindAllSortByFirstName() {
        List<User> userList = userDAO.findAll();
        List<User> sortedList = new ArrayList<>(userList);
        Comparator<User> groupByComparator = Comparator.comparing(User::getFirstName)
                .thenComparing(User::getLastName).thenComparing(User::getId);
        sortedList.sort(groupByComparator);

        assertIterableEquals(sortedList, userDAO.findAllSortByFirstName());
    }

    @Test
    public void testFindAllSortByLastName() {
        List<User> userList = userDAO.findAll();
        List<User> sortedList = new ArrayList<>(userList);
        Comparator<User> groupByComparator = Comparator.comparing(User::getLastName)
                .thenComparing(User::getFirstName).thenComparing(User::getId);
        sortedList.sort(groupByComparator);

        assertIterableEquals(sortedList, userDAO.findAllSortByLastName());
    }

    @Test
    public void testFindById() {
        assertFalse(userDAO.findById(15L).isPresent());
        assertTrue(userDAO.findById(1L).isPresent());
        assertEquals("Michael", userDAO.findById(1L).get().getFirstName());
    }

    @Test
    public void testSave() {
        User testUser = new User();
        testUser.setFirstName("Mike");
        testUser.setLastName("Wazowski");
        testUser = userDAO.save(testUser);
        assertEquals(4,userDAO.findAll().size());
        assertEquals(4,testUser.getId());
        User firstUser = userDAO.findById(1L).get();
        firstUser.setLastName("Krasinski");
        userDAO.save(firstUser);
        firstUser = userDAO.findById(1L).get();
        assertEquals("Krasinski", firstUser.getLastName());
    }

    @Test
    public void testDelete() {
        List<User> users = userDAO.findAll();
        assertEquals(3, users.size());

        userDAO.deleteById(3L);
        users = userDAO.findAll();
        assertEquals(2, users.size());
        assertFalse(userDAO.findById(3L).isPresent());
    }

    @Configuration
    @Import({TestJdbcConfig.class})
    static class TestUserDAOConfig {
        @Bean
        public UserDAO userDAO(EntityManagerFactory entityManagerFactory) {
            return new UserDAOImpl(entityManagerFactory);
        }
    }
}
