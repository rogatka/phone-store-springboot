package com.epam.store;

import com.epam.store.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstraintsTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void checkUserConstraintsViolation() {
        User user = new User();
        user.setFirstName("");
        user.setLastName("Alonso");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations.size()).isEqualTo(1);
        user = new User();
        user.setFirstName("Marcos");
        user.setLastName("");
        violations = validator.validate(user);
        assertThat(violations.size()).isEqualTo(1);
        user = new User();
        user.setFirstName("");
        user.setLastName("");
        violations = validator.validate(user);
        assertThat(violations.size()).isEqualTo(2);
        user = new User();
        user.setFirstName("Marcos");
        user.setLastName("Alonso");
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void checkAccountConstraintsViolation() {
        Account account = new Account();
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertThat(violations.size()).isEqualTo(1);
        account.setUser(new User());
        violations = validator.validate(account);
        assertTrue(violations.isEmpty());
        account = new Account();
        account.setUser(new User());
        account.setAmount(BigDecimal.valueOf(-1));
        violations = validator.validate(account);
        assertThat(violations.size()).isEqualTo(1);
        account.setAmount(BigDecimal.ZERO);
        violations = validator.validate(account);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void checkOrderConstraintsViolation() {
        Order order = new Order();
        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        assertThat(violations.size()).isEqualTo(1);
        order.setAccount(new Account());
        violations = validator.validate(order);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void checkOrderCardConstraintsViolation() {
        OrderCard orderCard = new OrderCard();
        Set<ConstraintViolation<OrderCard>> violations = validator.validate(orderCard);
        assertThat(violations.size()).isEqualTo(2);
        orderCard.setPhone(new Phone());
        violations = validator.validate(orderCard);
        assertThat(violations.size()).isEqualTo(1);
        orderCard.setItemCount(1L);
        violations = validator.validate(orderCard);
        assertTrue(violations.isEmpty());
        orderCard.setItemCount(-1L);
        violations = validator.validate(orderCard);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void checkOrderStatusHistoryConstraintsViolation() {
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        Set<ConstraintViolation<OrderStatusHistory>> violations = validator.validate(orderStatusHistory);
        assertThat(violations.size()).isEqualTo(3);
        orderStatusHistory.setOrder(new Order());
        violations = validator.validate(orderStatusHistory);
        assertThat(violations.size()).isEqualTo(2);
        orderStatusHistory.setOrderStatus(OrderStatus.NOT_STARTED);
        violations = validator.validate(orderStatusHistory);
        assertThat(violations.size()).isEqualTo(1);
        orderStatusHistory.setTimeStamp(LocalDateTime.now());
        violations = validator.validate(orderStatusHistory);
        assertTrue(violations.isEmpty());
        orderStatusHistory.setTimeStamp(LocalDateTime.now().minusSeconds(10));
        violations = validator.validate(orderStatusHistory);
        assertTrue(violations.isEmpty());
        orderStatusHistory.setTimeStamp(LocalDateTime.now().plusSeconds(10));
        violations = validator.validate(orderStatusHistory);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void checkPhoneConstraintsViolation() {
        Phone phone = new Phone();
        Set<ConstraintViolation<Phone>> violations = validator.validate(phone);
        assertThat(violations.size()).isEqualTo(3);
        phone.setModel("Samsung S25");
        violations = validator.validate(phone);
        assertThat(violations.size()).isEqualTo(2);
        phone.setCount(100L);
        violations = validator.validate(phone);
        assertThat(violations.size()).isEqualTo(1);
        phone.setPrice(BigDecimal.valueOf(10));
        violations = validator.validate(phone);
        assertTrue(violations.isEmpty());
        phone.setCount(-1L);
        violations = validator.validate(phone);
        assertThat(violations.size()).isEqualTo(1);
        phone.setCount(10L);
        violations = validator.validate(phone);
        assertTrue(violations.isEmpty());
        phone.setPrice(BigDecimal.valueOf(-1));
        violations = validator.validate(phone);
        assertThat(violations.size()).isEqualTo(1);
    }
}
