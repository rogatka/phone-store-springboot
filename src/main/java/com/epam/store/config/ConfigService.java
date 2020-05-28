package com.epam.store.config;

import com.epam.store.dao.*;
import com.epam.store.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;

@Configuration
@Import({ConfigDAO.class})
public class ConfigService {

    @Bean
    public UserService userService(UserDAO userDAO, AccountDAO accountDAO) {
        return new UserServiceImpl(userDAO, accountDAO);
    }

    @Bean
    public AccountService accountService(AccountDAO accountDAO, OrderDAO orderDAO) {
        return new AccountServiceImpl(accountDAO, orderDAO);
    }

    @Bean
    public OrderService orderService(OrderDAO orderDAO, PhoneDAO phoneDAO, OrderStatusHistoryDAO orderStatusHistoryDAO, OrderCardDAO orderCardDAO) {
        return new OrderServiceImpl(orderDAO, phoneDAO, orderStatusHistoryDAO, orderCardDAO);
    }

    @Bean
    public PhoneService phoneService(PhoneDAO phoneDAO, OrderCardDAO orderCardDAO) {
        return new PhoneServiceImpl(phoneDAO, orderCardDAO);
    }

    @Bean
    public OrderCardService orderCardService(OrderCardDAO orderCardDAO, OrderDAO orderDAO) {
        return new OrderCardServiceImpl(orderCardDAO, orderDAO);
    }

    @Bean
    public OrderStatusHistoryService orderStatusHistoryService(OrderStatusHistoryDAO orderStatusHistoryDAO) {
        return new OrderStatusHistoryServiceImpl(orderStatusHistoryDAO);
    }

}
