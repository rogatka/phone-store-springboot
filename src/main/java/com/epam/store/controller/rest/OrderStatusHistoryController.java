package com.epam.store.controller.rest;

import com.epam.store.entity.OrderStatus;
import com.epam.store.entity.OrderStatusHistory;
import com.epam.store.service.OrderStatusHistoryService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "orderStatusHistories")
public class OrderStatusHistoryController {
    private OrderStatusHistoryService orderStatusHistoryService;

    public OrderStatusHistoryController(OrderStatusHistoryService orderStatusHistoryService) {
        this.orderStatusHistoryService = orderStatusHistoryService;
    }


    @GetMapping
    public ResponseEntity<List<OrderStatusHistory>> findAll(@RequestParam(name = "orderId", required = false) String orderIdStr,
                                                            @RequestParam(name = "status", required = false) String orderStatus) {
        if (orderIdStr == null && orderStatus == null) {
            return new ResponseEntity<>(orderStatusHistoryService.findAll(), HttpStatus.OK);
        } else {
            if (orderIdStr == null) {
                try {
                    OrderStatus status = OrderStatus.valueOf(orderStatus);
                    return new ResponseEntity<>(orderStatusHistoryService.findAllByOrderStatus(status), HttpStatus.OK);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("No such status: " + orderStatus);
                }
            } else if (orderStatus == null) {
                try {
                    Long id = Long.parseLong(orderIdStr.trim());
                    List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryService.findAllByOrderId(id);
                    return new ResponseEntity<>(orderStatusHistoryList, HttpStatus.OK);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("order id is not a number: orderId=" + orderIdStr);
                }
            } else {
                Long id = Long.parseLong(orderIdStr.trim());
                List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryService.findAllByOrderId(id);
                OrderStatus status = OrderStatus.valueOf(orderStatus);
                List<OrderStatusHistory> orderStatusHistories = orderStatusHistoryService.findAllByOrderStatus(status);
                orderStatusHistoryList.retainAll(orderStatusHistories);
                return new ResponseEntity<>(orderStatusHistoryList, HttpStatus.OK);
            }
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderStatusHistory> findById(@PathVariable(name = "id") Long id) throws NotFoundException {
        Optional<OrderStatusHistory> orderStatusHistoryOptional = orderStatusHistoryService.findById(id);
        if (orderStatusHistoryOptional.isPresent()) {
            return new ResponseEntity<>(orderStatusHistoryOptional.get(), HttpStatus.OK);
        } else {
            throw new NotFoundException("order status history item with id = " + id + " not exist");
        }
    }

    @PostMapping
    public void createOrderStatusHistoryItem(@RequestBody OrderStatusHistory orderStatusHistory) {
        orderStatusHistoryService.save(orderStatusHistory);
    }

    @PutMapping("/{id}")
    public void updateOrderStatusHistoryItem(@RequestBody OrderStatusHistory orderStatusHistory,
                                             @PathVariable Long id) throws NotFoundException {
        if (orderStatusHistoryService.findById(id).isPresent()) {
            orderStatusHistoryService.save(orderStatusHistory);
        } else {
            throw new NotFoundException("order status history item with id = " + orderStatusHistory.getId() + " not exist");
        }
    }

    @DeleteMapping(path = "/{id}")
    public void deleteOrderStatusHistoryItem(@PathVariable(name = "id") Long id) throws NotFoundException {
        Optional<OrderStatusHistory> orderStatusHistoryOptional = orderStatusHistoryService.findById(id);
        if (orderStatusHistoryOptional.isPresent()) {
            orderStatusHistoryService.deleteById(id);
        } else {
            throw new NotFoundException("order status history item with id = " + id + " not exist");
        }
    }
}
