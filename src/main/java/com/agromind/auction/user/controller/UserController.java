package com.agromind.auction.user.controller;

import com.agromind.auction.user.dto.AuthResponse;
import com.agromind.auction.user.dto.LoginRequest;
import com.agromind.auction.user.model.User;
import com.agromind.auction.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        AuthResponse response=userService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(){
        return ResponseEntity.ok("Welcome to the secret profile area! Your JWT works perfectly.");
    }
}
