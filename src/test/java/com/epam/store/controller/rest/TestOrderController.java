package com.epam.store.controller.rest;

import com.epam.store.Main;
import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.Account;
import com.epam.store.entity.Order;
import com.epam.store.entity.OrderStatus;
import com.epam.store.service.AccountService;
import com.epam.store.service.OrderService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = {
        Main.class,
        TestJdbcConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestOrderController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DataSource dataSource;

    @Test
    public void getOrdersShouldReturn200() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("PROCESSING"))
                .andDo(print());
    }

    @Test
    public void getOrdersIfNoOrdersFoundShouldReturnEmptyListAndStatus200() throws Exception {
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("delete-tables.sql"), StandardCharsets.UTF_8));
        mockMvc.perform(get("/orders"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getOrderByIdShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/orders/{id}", 3))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("READY"))
                .andDo(print());
    }

    @Test
    public void getOrderByIdShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(get("/orders/{id}", 10))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void getOrdersByAccountIdShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/orders/account/{id}", 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[2].status").value("READY"))
                .andDo(print());
    }

    @Test
    public void getOrdersByAccountIdShouldReturnEmptyListAnd200IfNotFound() throws Exception {
        mockMvc.perform(get("/orders/account/{id}", 3))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void updateOrderShouldReturn200IfValid() throws Exception {
        Long id = 4L;
        Order order = orderService.findById(id).get();
        order.setStatus(OrderStatus.PROCESSING);
        order.setTotalSum(BigDecimal.valueOf(1000));
        ObjectMapper objectMapper = new ObjectMapper();
        String updateOrderJson = objectMapper.writeValueAsString(order);
        mockMvc.perform(put("/orders/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateOrderJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createOrderShouldReturn200IfValid() throws Exception {
        Long id = 2L;
        Account account = accountService.findById(id).get();
        Order order = new Order();
        order.setAccount(account);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(order);
        mockMvc.perform(post("/orders")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createOrderShouldReturn400IfAccountIsNull() throws Exception {
        Order order = new Order();
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(order);
        mockMvc.perform(post("/orders")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void deleteOrderShouldReturn200IfValidRequest() throws Exception {
        mockMvc.perform(delete("/orders/3"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deleteOrderShouldReturn400IfProcessing() throws Exception {
        mockMvc.perform(delete("/orders/2"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void deleteOrderShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(delete("/orders/6"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
