package com.epam.store.dao;

import com.epam.store.Main;
import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.OrderCard;
import com.epam.store.entity.Phone;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        Main.class,
        TestJdbcConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestPhoneDAO {

    @Autowired
    private PhoneDAO phoneDAO;

    @Autowired
    private OrderCardDAO orderCardDAO;

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
}
