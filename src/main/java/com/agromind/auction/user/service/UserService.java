package com.agromind.auction.user.service;

import com.agromind.auction.user.dto.AuthResponse;
import com.agromind.auction.user.dto.LoginRequest;
import com.agromind.auction.user.model.User;
import com.agromind.auction.user.repository.UserRepository;
import com.agromind.auction.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; //injecting jwt tool

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

    public AuthResponse loginUser(LoginRequest request){
        // 1. Find user by email
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new RuntimeException("User not found!"));
        // 2. Check if raw password matches the hashed password in DB
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new RuntimeException("Invalid Password");
        }
        // 3. Passwords match! Generate the VIP Wristband (Token)
        String token=jwtService.generateToken(user);
        return new AuthResponse(token,"Login Successful");


    }
}
