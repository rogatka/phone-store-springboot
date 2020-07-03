package utils.parsers;

import com.epam.store.configuration.ObjectMapperConfiguration;
import com.epam.store.entity.*;
import com.epam.store.utils.parsers.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {JsonParser.class, ObjectMapperConfiguration.class})
public class TestJsonParser {
    @Autowired
    private JsonParser jsonParser;
    private final static String JSON_OUTPUT = "{\"id\":1,\"order\":null,\"orderStatus\":2,\"timeStamp\":\"2020-07-02 10:10:00\"}";
    private final static String JSON_ACCOUNT_INPUT = "{\"id\":1,\"user\":{\"id\":null,\"firstName\":null,\"lastName\":null},\"amount\":12121.21, \"orders\":[]}";
    private final static String JSON_ACCOUNT_OUTPUT = "{\"id\":1,\"user\":{\"id\":null,\"firstName\":null,\"lastName\":null},\"amount\":12121.21}";

    @Test
    void serializeFromObjectShouldReturnCorrectJson() throws JsonProcessingException {
        String orderStatusHistoryJson = jsonParser.serialize(getOrderStatusHistory());
        assertEquals(JSON_OUTPUT, orderStatusHistoryJson);
        assertEquals(JSON_ACCOUNT_OUTPUT, jsonParser.serialize(getAccount()));
    }

    @Test
    void deserializeFromJsonShouldReturnCorrectObject() throws JsonProcessingException {
        assertThrows(JsonMappingException.class,() -> jsonParser.deserializeFromString(JSON_ACCOUNT_INPUT, new TypeReference<Account>() {
        }));
    }

    private OrderStatusHistory getOrderStatusHistory() {
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setId(1L);
        orderStatusHistory.setOrder(null);
        orderStatusHistory.setOrderStatus(OrderStatus.READY);
        orderStatusHistory.setTimeStamp(LocalDateTime.of(2020, Month.JULY, 2, 10, 10));
        return orderStatusHistory;
    }

    private Account getAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setUser(new User());
        account.setAmount(new BigDecimal("12121.2121").setScale(2, RoundingMode.HALF_UP));
        account.setOrders(Collections.emptyList());
        return account;
    }

}
