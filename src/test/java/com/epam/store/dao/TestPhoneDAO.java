package com.epam.store.dao;

import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.*;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = {TestPhoneDAO.TestPhoneDAOConfig.class})
@ExtendWith({SpringExtension.class})
@TestPropertySource("classpath:test-persistence.properties")
public class TestPhoneDAO {

    @Autowired
    private PhoneDAO phoneDAO;

    @Autowired
    private OrderCardDAO orderCardDAO;

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
        List<Phone> phones = phoneDAO.findAll();
        assertEquals(3, phones.size());
        Phone phone = new Phone();
        phone.setModel("Huawei");
        phone.setDescription("Camera240 Mpx");
        phone.setPrice(BigDecimal.valueOf(1234.56));
        phone.setCount(1000L);
        phoneDAO.save(phone);
        phones = phoneDAO.findAll();
        assertEquals(4, phones.size());
    }

    @Test
    public void testFindByModelName() {
        assertTrue(phoneDAO.findByModelName("Apple Iphone X20").isPresent());
        assertEquals("Samsung S20", phoneDAO.findByModelName("Samsung S20").get().getModel());
        assertFalse(phoneDAO.findByModelName("Samsung").isPresent());
    }

    @Test
    public void testFindById() {
        assertFalse(phoneDAO.findById(4L).isPresent());
        assertTrue(phoneDAO.findById(1L).isPresent());
        assertEquals("Samsung S20", phoneDAO.findById(1L).get().getModel());
    }

    @Test
    public void testSave() {
        assertFalse(phoneDAO.findById(4L).isPresent());
        Phone phone = new Phone();
        phone.setModel("Huawei");
        phone.setDescription("Camera240 Mpx");
        phone.setPrice(BigDecimal.valueOf(1234.56));
        phone.setCount(1000L);
        phoneDAO.save(phone);
        assertTrue(phoneDAO.findById(4L).isPresent());
    }

    @Test
    public void testSaveAll() {
        assertEquals(3, phoneDAO.findAll().size());
        Phone huawei = new Phone();
        huawei.setModel("Huawei");
        huawei.setDescription("Camera 240 Mpx");
        huawei.setPrice(BigDecimal.valueOf(1234.56));
        huawei.setCount(1000L);
        Phone onePlus = new Phone();
        onePlus.setModel("OnePlus 10");
        onePlus.setDescription("Camera 140 Mpx");
        onePlus.setPrice(BigDecimal.valueOf(8000.56));
        onePlus.setCount(100L);
        List<Phone> phones = new ArrayList<>();
        phones.add(huawei);
        phones.add(onePlus);
        phoneDAO.saveAll(phones);
        assertEquals(5, phoneDAO.findAll().size());
    }

    @Test
    public void testDeleteById() {
        List<Phone> phones = phoneDAO.findAll();
        assertEquals(3, phones.size());
        List<OrderCard> orderCards = orderCardDAO.findAllByPhoneId(1L);
        for (OrderCard orderCard: orderCards) {
            orderCardDAO.deleteById(orderCard.getId());
        }
        phoneDAO.deleteById(1L);
        phones = phoneDAO.findAll();
        assertEquals(2, phones.size());
    }

    @Configuration
    @Import({TestJdbcConfig.class})
    static class TestPhoneDAOConfig {
        @Bean
        public PhoneDAO phoneDAO(EntityManagerFactory entityManagerFactory) {
            return new PhoneDAOImpl(entityManagerFactory);
        }
        @Bean
        public OrderCardDAO orderCardDAO(EntityManagerFactory entityManagerFactory) {
            return new OrderCardDAOImpl(entityManagerFactory);
        }
    }
}
