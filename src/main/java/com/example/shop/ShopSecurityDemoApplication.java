package com.example.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import com.example.shop.model.UserAccount;
import com.example.shop.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ShopSecurityDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopSecurityDemoApplication.class, args);
    }
    @Bean CommandLineRunner seedAdmin(UserAccountRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.findByEmailIgnoreCase("admin@example.com").isEmpty()) {
                UserAccount admin = new UserAccount("admin", "admin@example.com",
                        encoder.encode("123456"));
                admin.setRole("ADMIN"); admin.setEnabled(true); repository.save(admin);
            }
        };
    }
}
