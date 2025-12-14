package com.sweetshop.controller;

import com.sweetshop.model.User;
import com.sweetshop.repository.UserRepository;
import com.sweetshop.security.JwtUtil;
import com.sweetshop.service.CustomUserDetailsService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private CustomUserDetailsService userDetailsService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) return ResponseEntity.badRequest().body("Username exists");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRole() == null) user.setRole("user"); 
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try { authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())); } 
        catch (Exception e) { return ResponseEntity.status(401).body("Invalid credentials"); }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsername());
        String role = userRepository.findByUsername(req.getUsername()).get().getRole();
        return ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(userDetails, role), "role", role));
    }
}
@Data class AuthRequest { private String username; private String password; }
