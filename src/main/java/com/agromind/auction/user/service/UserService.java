package com.agromind.auction.user.service;

import com.agromind.auction.user.model.User;
import com.agromind.auction.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        // 1. Check if email exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered!");
        }

        // 2. Hash the password securely
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. Save to database
        return userRepository.save(user);
    }
}
