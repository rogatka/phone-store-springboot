package com.epam.store.controller.rest;

import com.epam.store.entity.Phone;
import com.epam.store.service.PhoneService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "phones")
public class PhoneController {
    private PhoneService phoneService;

    public PhoneController(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @GetMapping
    public ResponseEntity<List<Phone>> findAll(@RequestParam(name = "model", required = false) String model) throws NotFoundException {
        if (model == null) {
            return new ResponseEntity<>(phoneService.findAll(), HttpStatus.OK);
        } else {
            Optional<Phone> phoneOptional = phoneService.findByModelName(model);
            if (phoneOptional.isPresent()) {
                return new ResponseEntity<>(Collections.singletonList(phoneOptional.get()), HttpStatus.OK);
            } else {
                throw new NotFoundException("phone with model name = " + model + " not exist");
            }
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Phone> findById(@PathVariable(name = "id") Long id) throws NotFoundException {
        Optional<Phone> phoneOptional = phoneService.findById(id);
        if (phoneOptional.isPresent()) {
            return new ResponseEntity<>(phoneOptional.get(), HttpStatus.OK);
        } else {
            throw new NotFoundException("phone with id = " + id + " not exist");
        }
    }

    @PostMapping
    public void savePhone(@RequestBody Phone phone) {
        phoneService.save(phone);
    }

    @PutMapping("/{id}")
    public void updatePhone(@RequestBody Phone phone, @PathVariable Long id) throws NotFoundException {
        if (phoneService.findById(id).isPresent()) {
            phoneService.save(phone);
        } else {
            throw new NotFoundException("phone with id = " + phone.getId() + " not exist");
        }
    }

    @DeleteMapping(path = "/{id}")
    public void deletePhone(@PathVariable(name = "id") Long id) throws NotFoundException {
        Optional<Phone> phoneOptional = phoneService.findById(id);
        if (phoneOptional.isPresent()) {
            phoneService.deleteById(id);
        } else {
            throw new NotFoundException("phone with id = " + id + " not exist");
        }
    }
}
