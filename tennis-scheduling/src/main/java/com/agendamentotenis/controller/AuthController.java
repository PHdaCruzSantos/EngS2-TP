package com.agendamentotenis.controller;

import com.agendamentotenis.security.JwtConfig;
import com.agendamentotenis.model.User;
import com.agendamentotenis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Permite requisições do frontend
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        return userService.findByEmail(loginUser.getEmail())
                .filter(user -> passwordEncoder.matches(loginUser.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtConfig.generateToken(user.getEmail());
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("user", user);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Credenciais inválidas");
                    return ResponseEntity.badRequest().body(errorResponse);
                });
    }
}