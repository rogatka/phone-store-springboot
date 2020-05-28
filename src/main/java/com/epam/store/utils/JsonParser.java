package com.epam.store.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;


public class JsonParser {
    private static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T>T deserializeFromString(String content, TypeReference<T> typeReference) throws JsonProcessingException {
        return objectMapper.readValue(content, typeReference);
    }
    public static <T>T deserializeFromFile(File file, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(file, typeReference);
    }

    public static String serialize(Object content) throws JsonProcessingException {
        return objectMapper.writeValueAsString(content);
    }
}
