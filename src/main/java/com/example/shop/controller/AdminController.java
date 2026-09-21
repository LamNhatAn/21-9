package com.example.shop.controller;

import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserAccountRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    private final UserAccountRepository users;
    private final ProductRepository products;

    public AdminController(UserAccountRepository users, ProductRepository products) {
        this.users = users;
        this.products = products;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("userCount", users.count());
        model.addAttribute("productCount", products.count());
        return "dashboard";
    }
}
