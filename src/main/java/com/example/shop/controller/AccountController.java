package com.example.shop.controller;

import com.example.shop.model.UserAccount;
import com.example.shop.service.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
    private final AccountService accounts;
    public AccountController(AccountService accounts) { this.accounts = accounts; }
    @GetMapping("/profile") public String profile(Authentication authentication, Model model) {
        UserAccount account = accounts.byUsername(authentication.getName());
        model.addAttribute("account", account); return "profile";
    }
}
