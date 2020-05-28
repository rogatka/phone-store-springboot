package com.epam.store.controller.rest;

import com.epam.store.entity.OrderCard;
import com.epam.store.service.OrderCardService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "orderCards")
public class OrderCardController {
    private OrderCardService orderCardService;

    public OrderCardController(OrderCardService orderCardService) {
        this.orderCardService = orderCardService;
    }

    @GetMapping
    public ResponseEntity<List<OrderCard>> findAll(@RequestParam(name = "sortBy", required = false) String sortVariable) {
        if (sortVariable != null && sortVariable.equalsIgnoreCase("count")) {
            return new ResponseEntity<>(orderCardService.findAllSortByItemCountDesc(), HttpStatus.OK);
        }
        return new ResponseEntity<>(orderCardService.findAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderCard> findById(@PathVariable(name = "id") Long orderCardId) throws NotFoundException {
        Optional<OrderCard> orderCardResult = orderCardService.findById(orderCardId);
        if (orderCardResult.isPresent()) {
            return new ResponseEntity<>(orderCardResult.get(), HttpStatus.OK);
        } else {
            throw new NotFoundException("order card with id = " + orderCardId + " not exist");
        }
    }

    @GetMapping(path = "/order/{id}")
    public ResponseEntity<List<OrderCard>> findByOrderId(@PathVariable(name = "id") Long orderId) throws NotFoundException {
        List<OrderCard> orderCards = orderCardService.findAllByOrderId(orderId);
        return new ResponseEntity<>(orderCards, HttpStatus.OK);
    }

    @GetMapping(path = "/phone/{id}")
    public ResponseEntity<List<OrderCard>> findByPhoneId(@PathVariable(name = "id") Long phoneId) throws NotFoundException {
        List<OrderCard> orderCards = orderCardService.findAllByPhoneId(phoneId);
        return new ResponseEntity<>(orderCards, HttpStatus.OK);
    }

    @PostMapping
    public void createOrderCard(@RequestBody OrderCard orderCard) {
        orderCardService.save(orderCard);
    }

    @PutMapping("/{id}")
    public void updateOrderCard(@RequestBody OrderCard orderCard, @PathVariable Long id) throws NotFoundException {
        if (orderCardService.findById(id).isPresent()) {
            orderCardService.save(orderCard);
        } else {
            throw new NotFoundException("order card with id = " + orderCard.getId() + " not exist");
        }
    }

    @DeleteMapping(path = "/{id}")
    public void deleteOrderCard(@PathVariable(name = "id") Long orderCardId) throws NotFoundException {
        Optional<OrderCard> orderCardResult = orderCardService.findById(orderCardId);
        if (orderCardResult.isPresent()) {
            orderCardService.deleteById(orderCardId);
        } else {
            throw new NotFoundException("order card with id = " + orderCardId + " not exist");
        }
    }
}
