package com.epam.store.controller.rest;

import com.epam.store.config.WebJavaConfig;
import com.epam.store.entity.Phone;
import com.epam.store.service.PhoneService;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

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
public class TestPhoneController {
    private MockMvc mockMvc;

    @Autowired
    private PhoneService phoneService;

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
    public void getPhonesShouldReturn200() throws Exception {
        mockMvc.perform(get("/phones"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[1].model").value("Apple Iphone X20"))
                .andExpect(jsonPath("$[2].count").value(15000))
                .andDo(print());
    }

    @Test
    public void getPhonesIfNotFoundShouldReturnEmptyListAndStatus200() throws Exception {
        ScriptUtils.executeSqlScript(
                dataSource.getConnection(),
                new EncodedResource(new ClassPathResource("delete-tables.sql"), StandardCharsets.UTF_8));
        mockMvc.perform(get("/phones"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getPhoneByIdShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/phones/{id}", 3))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value("Xiaomi Mi50"))
                .andDo(print());
    }

    @Test
    public void getPhoneByIdShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(get("/phones/{id}", 10))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void getPhoneByModelNameShouldReturn200IfFound() throws Exception {
        mockMvc.perform(get("/phones?model={name}", "Samsung S20"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].model").value("Samsung S20"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void updatePhoneShouldReturn200IfValid() throws Exception {
        Long id = 2L;
        Phone phone = phoneService.findById(id).get();
        phone.setModel("Samsung X100");
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(phone);
        mockMvc.perform(put("/phones/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createPhoneShouldReturn200IfValid() throws Exception {
        Phone phone = new Phone();
        phone.setModel("Huawei T1000");
        phone.setCount(100L);
        phone.setPrice(BigDecimal.valueOf(450.50));
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(phone);
        mockMvc.perform(post("/phones")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createPhoneShouldReturn400IfPhoneIsNull() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(null);
        mockMvc.perform(post("/phones")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void deletePhoneByIdShouldReturn404IfThereAreProcessingOrders() throws Exception {
        mockMvc.perform(delete("/phones/2"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void deletePhoneByIdShouldReturn200IfNoProcessingOrders() throws Exception {
        mockMvc.perform(delete("/phones/3"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deletePhoneShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(delete("/phones/10"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
