package com.epam.store.dao;

import com.epam.store.Main;
import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        Main.class,
        TestJdbcConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestUserDAO {

    @Autowired
    private UserDAO userDAO;

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
}
