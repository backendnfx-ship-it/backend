package com.ultimateflange.controller;


import com.ultimateflange.dto.ApiResponse;
import com.ultimateflange.dto.AuthResponse;
import com.ultimateflange.dto.LoginRequest;
import com.ultimateflange.dto.RegisterRequest;
import com.ultimateflange.model.User;
import com.ultimateflange.service.JwtService;
import com.ultimateflange.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtService.generateToken(user.getEmail());

            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .token(token)
                    .user(user)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Invalid credentials")
            );
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request);
            return ResponseEntity.ok(
                    ApiResponse.success("Registration successful", user)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }
}