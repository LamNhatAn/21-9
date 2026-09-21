package com.example.shop.service;

import com.example.shop.model.UserAccount;
import com.example.shop.repository.UserAccountRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService implements UserDetailsService {
    private final UserAccountRepository repository;
    private final PasswordEncoder encoder;
    private final long otpMinutes;
    public AccountService(UserAccountRepository repository, PasswordEncoder encoder,
                          @Value("${app.otp.minutes:5}") long otpMinutes) {
        this.repository = repository; this.encoder = encoder; this.otpMinutes = otpMinutes;
    }
    @Override public UserDetails loadUserByUsername(String identifier) {
        UserAccount account = repository.findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
        return User.withUsername(account.getUsername()).password(account.getPassword())
                .roles(account.getRole()).disabled(!account.isEnabled()).build();
    }
    @Transactional
    public UserAccount register(String username, String email, String rawPassword) {
        return register(username, email, null, rawPassword);
    }
    @Transactional
    public UserAccount register(String username, String email, String fullName, String rawPassword) {
        if (repository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(username, email))
            throw new IllegalArgumentException("Username hoặc email đã tồn tại");
        UserAccount account = new UserAccount(username.trim(), email.trim().toLowerCase(),
                encoder.encode(rawPassword));
        account.setFullName(fullName == null ? null : fullName.trim());
        issueOtp(account); return repository.save(account);
    }
    @Transactional public String issueOtp(UserAccount account) {
        String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        account.setOtp(otp); account.setOtpExpiresAt(Instant.now().plus(Duration.ofMinutes(otpMinutes)));
        return otp;
    }
    @Transactional public boolean verify(UserAccount account, String otp) {
        if (account.getOtp() == null || !account.getOtp().equals(otp)
                || account.getOtpExpiresAt() == null || account.getOtpExpiresAt().isBefore(Instant.now()))
            return false;
        account.setEnabled(true); account.setOtp(null); account.setOtpExpiresAt(null); repository.save(account); return true;
    }
    public UserAccount byEmail(String email) {
        return repository.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
    }
    @Transactional public void changePassword(UserAccount account, String raw) {
        account.setPassword(encoder.encode(raw)); account.setOtp(null); account.setOtpExpiresAt(null); repository.save(account);
    }
    public UserAccount byUsername(String username) {
        return repository.findByUsernameIgnoreCaseOrEmailIgnoreCase(username, username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
