package com.example.shop.controller;

import com.example.shop.model.UserAccount;
import com.example.shop.service.AccountService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
public class AuthController {
    private final AccountService accounts;
    public AuthController(AccountService accounts) { this.accounts = accounts; }
    @GetMapping({"/", "/login"}) public String login(@RequestParam(required=false) String error,
                                                       @RequestParam(required=false) String logout, Model model) {
        if (error != null) model.addAttribute("message", "Thông tin đăng nhập không đúng hoặc tài khoản chưa xác thực.");
        if (logout != null) model.addAttribute("message", "Bạn đã đăng xuất.");
        return "login";
    }
    @GetMapping("/examples") public String examples() { return "examples"; }
    @GetMapping("/register") public String register() { return "register"; }
    @PostMapping("/register") public String register(@RequestParam @NotBlank String username,
            @RequestParam @Email @NotBlank String email, @RequestParam(required=false) String fullName,
            @RequestParam @Size(min=6) String password,
            HttpSession session, Model model) {
        try {
            UserAccount account = accounts.register(username, email, fullName, password);
            session.setAttribute("otpEmail", account.getEmail());
            model.addAttribute("notice", "OTP demo của bạn: " + account.getOtp());
            return "verify";
        } catch (IllegalArgumentException ex) { model.addAttribute("message", ex.getMessage()); return "register"; }
    }
    @GetMapping("/verify") public String verifyPage() { return "verify"; }
    @PostMapping("/verify") public String verify(@RequestParam String otp, HttpSession session, Model model) {
        String email = (String) session.getAttribute("otpEmail");
        if (email == null) { model.addAttribute("message", "Phiên xác thực đã hết hạn."); return "register"; }
        UserAccount account = accounts.byEmail(email);
        if (!accounts.verify(account, otp)) { model.addAttribute("message", "OTP không đúng hoặc đã hết hạn."); return "verify"; }
        return "redirect:/login?verified";
    }
    @GetMapping("/forgot-password") public String forgot() { return "forgot-password"; }
    @PostMapping("/forgot-password") public String forgot(@RequestParam @Email String email,
                                                           HttpSession session, Model model) {
        try {
            UserAccount account = accounts.byEmail(email);
            String otp = accounts.issueOtp(account);
            session.setAttribute("resetEmail", email.toLowerCase());
            model.addAttribute("notice", "OTP demo của bạn: " + otp);
            return "reset-password";
        } catch (IllegalArgumentException ex) { model.addAttribute("message", ex.getMessage()); return "forgot-password"; }
    }
    @GetMapping("/reset-password") public String reset() { return "reset-password"; }
    @PostMapping("/reset-password") public String reset(@RequestParam String otp, @RequestParam @Size(min=6) String password,
                                                         HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) { model.addAttribute("message", "Phiên đặt lại đã hết hạn."); return "forgot-password"; }
        UserAccount account = accounts.byEmail(email);
        if (!accounts.verify(account, otp)) { model.addAttribute("message", "OTP không đúng hoặc đã hết hạn."); return "reset-password"; }
        accounts.changePassword(account, password); session.invalidate(); return "redirect:/login?reset";
    }
}
