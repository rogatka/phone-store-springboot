package com.epam.store.controller.rest;

import com.epam.store.Main;
import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.Account;
import com.epam.store.entity.Order;
import com.epam.store.entity.OrderStatus;
import com.epam.store.entity.User;
import com.epam.store.service.AccountService;
import com.epam.store.service.OrderService;
import com.epam.store.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = {
        Main.class,
        TestJdbcConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestAccountController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DataSource dataSource;

    @Test
    public void getAccountsShouldReturn200() throws Exception {
        int expectedValue1 = 1;
        double expectedValue = 24500.57;
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(expectedValue1))
                .andExpect(jsonPath("$[0].amount").value(expectedValue))
                .andDo(print());
    }

    @Test
    public void getAccountsIfNoAccountsFoundShouldReturnEmptyListAndStatus200() throws Exception {
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("delete-tables.sql"), StandardCharsets.UTF_8));
        mockMvc.perform(get("/accounts"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getAccountByIdShouldReturn200IfFound() throws Exception {
        int id = 2;
        double expectedAmount = 112000.45;
        mockMvc.perform(get("/accounts/{id}", id))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(expectedAmount))
                .andDo(print());
    }

    @Test
    public void getAccountByIdShouldReturn404IfNotFound() throws Exception {
        int notExistAccountId = 10;
        mockMvc.perform(get("/accounts/{id}", 10))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void getAccountByUserIdShouldReturn200IfFound() throws Exception {
        int userId = 1;
        double expectedAmount = 24500.57;
        mockMvc.perform(get("/accounts/user/{id}", userId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(expectedAmount))
                .andDo(print());
    }

    @Test
    public void getAccountByUserIdShouldReturn404IfNotFound() throws Exception {
        int userId = 10;
        mockMvc.perform(get("/accounts/user/{id}", userId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void updateAccountShouldReturn200IfValid() throws Exception {
        Long id = 1L;
        Account account = accountService.findById(id).get();
        account.setAmount(BigDecimal.valueOf(123456.78));
        ObjectMapper objectMapper = new ObjectMapper();
        String updateAccountJson = objectMapper.writeValueAsString(account);
        mockMvc.perform(put("/accounts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAccountJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void updateAccountShouldReturn400IfAmountIsNegative() throws Exception {
        Long id = 1L;
        Account account = accountService.findById(id).get();
        account.setAmount(BigDecimal.valueOf(-1000));
        User user = userService.findById(id).get();
        account.setUser(user);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateAccountJson = objectMapper.writeValueAsString(account);
        mockMvc.perform(put("/accounts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAccountJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void updateAccountShouldReturn400IfAmountIsNull() throws Exception {
        Long id = 1L;
        Account account = accountService.findById(id).get();
        account.setAmount(null);
        User user = userService.findById(id).get();
        account.setUser(user);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateAccountJson = objectMapper.writeValueAsString(account);
        mockMvc.perform(put("/accounts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAccountJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void createAccountShouldReturn200IfValid() throws Exception {
        Long id = 1L;
        Account account = accountService.findByUserId(id).get();
        account.setAmount(BigDecimal.valueOf(123456.78));
        User user = userService.findById(id).get();
        account.setUser(user);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateAccountJson = objectMapper.writeValueAsString(account);
        mockMvc.perform(post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAccountJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createAccountShouldReturn400IfUserIsNull() throws Exception {
        Account account = new Account();
        account.setAmount(BigDecimal.valueOf(123456.78));
        account.setUser(null);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateAccountJson = objectMapper.writeValueAsString(account);
        mockMvc.perform(post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateAccountJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void deleteAccountShouldReturn200IfValidRequest() throws Exception {
        List<Order> orders = orderService.findAllByAccountId(2L);
        for (Order order: orders) {
            order.setStatus(OrderStatus.READY);
            order.setTotalSum(BigDecimal.ONE);
            orderService.save(order);
        }
        mockMvc.perform(delete("/accounts/2"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deleteAccountShouldReturn400IfThereIsUnfinishedOrders() throws Exception {
        mockMvc.perform(delete("/accounts/1"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void deleteAccountShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(delete("/accounts/5"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
