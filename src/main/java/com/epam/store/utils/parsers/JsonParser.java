package com.epam.store.utils.parsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class JsonParser {
    private final ObjectMapper objectMapper;

    @Autowired
    public JsonParser(@Qualifier("customObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T>T deserializeFromString(String content, TypeReference<T> typeReference) throws JsonProcessingException {
        return objectMapper.readValue(content, typeReference);
    }
    public <T>T deserializeFromFile(File file, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(file, typeReference);
    }

    public String serialize(Object content) throws JsonProcessingException {
        return objectMapper.writeValueAsString(content);
    }
}
