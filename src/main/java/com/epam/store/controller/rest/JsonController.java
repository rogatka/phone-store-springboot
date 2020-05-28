package com.epam.store.controller.rest;

import com.epam.store.entity.Phone;
import com.epam.store.service.PhoneService;
import com.epam.store.utils.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/json")
public class JsonController {

    public static final String SUCCESS_MESSAGE = ">>>Deserialization From Json To Db: Success! <br> <p><a href='serialize'>Check result</a></p>";

    private PhoneService phoneService;

    public JsonController(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @GetMapping("/serialize")
    public @ResponseBody String serializeToJsonFromDb() throws JsonProcessingException {
        List<Phone> phones = phoneService.findAll();
        return JsonParser.serialize(phones);
    }

    @GetMapping("/deserialize")
    public ResponseEntity deserializeFromFileAndSaveToDb() throws IOException, URISyntaxException {
        URI resource = Objects.requireNonNull(this.getClass().getClassLoader().getResource("phones.json")).toURI();
        List<Phone> phones = JsonParser.deserializeFromFile(new File(resource), new TypeReference<List<Phone>>() {
        });
        phoneService.saveAll(phones);
        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_MESSAGE);
    }

}
