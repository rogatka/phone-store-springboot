package com.epam.store.controller.rest;

import com.epam.store.Main;
import com.epam.store.config.TestJdbcConfig;
import com.epam.store.entity.Order;
import com.epam.store.entity.OrderCard;
import com.epam.store.entity.Phone;
import com.epam.store.service.OrderCardService;
import com.epam.store.service.OrderService;
import com.epam.store.service.PhoneService;
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
public class TestOrderCardController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderCardService orderCardService;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DataSource dataSource;

    @Test
    public void getOrderCardsShouldReturn200() throws Exception {
        mockMvc.perform(get("/orderCards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[4].order.id").value(3))
                .andExpect(jsonPath("$[4].phone.id").value(2))
                .andDo(print());
    }

    @Test
    public void getOrderCardsByItemCountDescShouldReturn200AndSortedItems() throws Exception {
        mockMvc.perform(get("/orderCards?sortBy=count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].itemCount").value(6))
                .andExpect(jsonPath("$[1].itemCount").value(5))
                .andDo(print());
    }

    @Test
    public void getOrdersIfNotFoundShouldReturnEmptyListAndStatus200() throws Exception {
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("delete-tables.sql"), StandardCharsets.UTF_8));
        mockMvc.perform(get("/orderCards"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getOrderCardByIdShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/orderCards/{id}", 3))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.order.id").value(2))
                .andDo(print());
    }

    @Test
    public void getOrderCardByIdShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(get("/orderCards/{id}", 10))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void getOrderCardsByOrderIdShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/orderCards/order/{id}", 5))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].order.id").value(5))
                .andDo(print());
    }

    @Test
    public void getOrderCardsByPhoneIdShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/orderCards/phone/{id}", 2))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].phone.id").value(2))
                .andDo(print());
    }


    @Test
    public void updateOrderCardShouldReturn200IfValid() throws Exception {
        Long id = 6L;
        OrderCard orderCard = orderCardService.findById(id).get();
        orderCard.setItemCount(5L);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(orderCard);
        System.out.println(updateJson);
        mockMvc.perform(put("/orderCards/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createOrderCardShouldReturn200IfValid() throws Exception {
        Long id = 1L;
        Phone phone = phoneService.findById(id).get();
        Order order = orderService.findById(id).get();
        OrderCard orderCard = new OrderCard();
        orderCard.setItemCount(5L);
        orderCard.setOrder(order);
        orderCard.setPhone(phone);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(orderCard);
        mockMvc.perform(post("/orderCards")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createOrderCardShouldReturn400IfOrderIsNull() throws Exception {
        OrderCard orderCard = new OrderCard();
        Phone phone = phoneService.findById(1L).get();
        orderCard.setPhone(phone);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(orderCard);
        mockMvc.perform(post("/orderCards")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void createOrderCardShouldReturn400IfPhoneIsNull() throws Exception {
        OrderCard orderCard = new OrderCard();
        Order order = orderService.findById(1L).get();
        orderCard.setOrder(order);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(orderCard);
        mockMvc.perform(post("/orderCards")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void createOrderCardShouldReturn400IfItemCountIsNull() throws Exception {
        OrderCard orderCard = new OrderCard();
        Order order = orderService.findById(1L).get();
        orderCard.setOrder(order);
        Phone phone = phoneService.findById(1L).get();
        orderCard.setPhone(phone);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(orderCard);
        mockMvc.perform(post("/orderCards")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void deleteOrderCardShouldReturn200IfValidRequest() throws Exception {
        mockMvc.perform(delete("/orderCards/5"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deleteOrderCardShouldReturn400IfProcessing() throws Exception {
        mockMvc.perform(delete("/orderCards/3"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void deleteOrderShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(delete("/orderCards/10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
