package com.epam.store.controller.rest;

import com.epam.store.config.WebJavaConfig;
import com.epam.store.entity.*;
import com.epam.store.service.OrderCardService;
import com.epam.store.service.OrderService;
import com.epam.store.service.OrderStatusHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        WebJavaConfig.class
})
@WebAppConfiguration
@EnableWebMvc
@TestPropertySource("classpath:test-persistence.properties")
public class TestOrderStatusHistoryController {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderStatusHistoryService orderStatusHistoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderCardService orderCardService;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        ScriptUtils.executeSqlScript(dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("create-tables.sql"), StandardCharsets.UTF_8));
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("populate-tables.sql"), StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() throws Exception {
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("drop-tables.sql"), StandardCharsets.UTF_8));
    }

    @Test
    public void getOrderStatusHistoriesShouldReturn200() throws Exception {
        mockMvc.perform(get("/orderStatusHistories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[4].orderStatus").value("PROCESSING"))
                .andExpect(jsonPath("$[6].order.id").value(4))
                .andDo(print());
    }

    @Test
    public void getOrderStatusHistoriesIfNotFoundShouldReturnEmptyListAndStatus200() throws Exception {
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("delete-tables.sql"), StandardCharsets.UTF_8));
        mockMvc.perform(get("/orderStatusHistories"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getOrderStatusHistoryByIdShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/orderStatusHistories/{id}", 7))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.order.id").value(4))
                .andDo(print());
    }

    @Test
    public void getOrderStatusHistoryByIdShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(get("/orderStatusHistories/{id}", 15))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void getOrderStatusHistoriesByOrderIdShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/orderStatusHistories?orderId={id}", 3))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].orderStatus").value("NOT_STARTED"))
                .andDo(print());
    }

    @Test
    public void getOrderStatusHistoriesByOrderStatusIdShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/orderStatusHistories?status={status}", "PROCESSING"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].orderStatus").value("PROCESSING"))
                .andDo(print());
    }


    @Test
    public void updateOrderStatusHistoryShouldReturn200IfValid() throws Exception {
        Long id = 8L;
        OrderStatusHistory orderStatusHistory = orderStatusHistoryService.findById(id).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("2020-05-11 16:24:11", formatter);
        orderStatusHistory.setTimeStamp(localDateTime);
        String updateJson = objectMapper.writeValueAsString(orderStatusHistory);
        mockMvc.perform(put("/orderStatusHistories/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createOrderStatusHistoryShouldReturn200IfValid() throws Exception {
        Long id = 2L;
        Order orderFromDb = orderService.findById(id).get();
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        Order order = new Order();
        order.setId(orderFromDb.getId());
        order.setTotalSum(orderFromDb.getTotalSum());
        order.setAccount(orderFromDb.getAccount());
        order.setStatus(orderFromDb.getStatus());
        orderStatusHistory.setOrder(order);
        orderStatusHistory.setTimeStamp(LocalDateTime.now());
        orderStatusHistory.setOrderStatus(OrderStatus.READY);
//        JavaTimeModule module = new JavaTimeModule();
//        LocalDateTimeDeserializer localDateTimeDeserializer =  new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
//        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
//        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
//                .modules(module)
//                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//                .build();
        String updateJson = objectMapper.writeValueAsString(orderStatusHistory);
        System.out.println(updateJson);
        mockMvc.perform(post("/orderStatusHistories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createOrderStatusHistoryShouldReturn400IfOrderIsNull() throws Exception {
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrder(null);
        orderStatusHistory.setTimeStamp(LocalDateTime.now());
        orderStatusHistory.setOrderStatus(OrderStatus.READY);
        String updateJson = objectMapper.writeValueAsString(orderStatusHistory);
        System.out.println(updateJson);
        mockMvc.perform(post("/orderStatusHistories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void createOrderStatusHistoryShouldReturn400IfStatusIsNull() throws Exception {
        Long id = 2L;
        Order order = orderService.findById(id).get();
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrder(order);
        orderStatusHistory.setTimeStamp(LocalDateTime.now());
        orderStatusHistory.setOrderStatus(null);
        String updateJson = objectMapper.writeValueAsString(orderStatusHistory);
        mockMvc.perform(post("/orderStatusHistories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void createOrderStatusHistoryShouldReturn400IfTimestampIsNull() throws Exception {
        Long id = 2L;
        Order order = orderService.findById(id).get();
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrder(order);
        orderStatusHistory.setTimeStamp(null);
        orderStatusHistory.setOrderStatus(OrderStatus.READY);
        String updateJson = objectMapper.writeValueAsString(orderStatusHistory);
        mockMvc.perform(post("/orderStatusHistories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void deleteOrderCardShouldReturn200IfValidRequest() throws Exception {
        mockMvc.perform(delete("/orderStatusHistories/5"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deleteOrderShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(delete("/orderStatusHistories/15"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
