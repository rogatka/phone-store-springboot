package com.epam.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = "/")
public class RootController {
    @GetMapping
    public String getRootPage() {
        return "index";
    }

    @GetMapping("accounts/{id}/info")
    public String getAccountInfo(@PathVariable Long id) {
        return "account-info";
    }

    @GetMapping("users/{userId}/addAccount")
    public String addAccount(@PathVariable Long userId) {
        return "add-account-form";
    }

    @GetMapping("orders/{orderId}/info")
    public String getOrderInfo(@PathVariable Long orderId) {
        return "order-info";
    }
}
