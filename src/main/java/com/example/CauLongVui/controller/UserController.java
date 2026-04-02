package com.example.CauLongVui.controller;

import com.example.CauLongVui.dto.ApiResponse;
import com.example.CauLongVui.dto.AuthResponse;
import com.example.CauLongVui.entity.User;
import com.example.CauLongVui.repository.UserRepository;
import com.example.CauLongVui.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuthResponse>>> getAllUsers() {
        List<AuthResponse> users = userRepository.findAll().stream()
                .map(u -> AuthResponse.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole())
                        .walletBalance(walletService.getBalanceForUser(u.getId()))
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<AuthResponse>> updateRole(
            @PathVariable Long id,
            @RequestParam String role
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nguoi dung khong ton tai"));
        user.setRole(User.Role.valueOf(role.toUpperCase()));
        userRepository.save(user);
        AuthResponse resp = AuthResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .walletBalance(walletService.getBalanceForUser(user.getId()))
                .build();
        return ResponseEntity.ok(ApiResponse.success("Cap nhat quyen thanh cong", resp));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<AuthResponse>> toggleActive(
            @PathVariable Long id,
            @RequestParam boolean active
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nguoi dung khong ton tai"));
        user.setActive(active);
        userRepository.save(user);
        AuthResponse resp = AuthResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .membershipTier(user.getMembershipTier())
                .membershipExpiry(user.getMembershipExpiry())
                .walletBalance(walletService.getBalanceForUser(user.getId()))
                .build();
        return ResponseEntity.ok(ApiResponse.success(
                active ? "Da kich hoat tai khoan" : "Da vo hieu hoa tai khoan",
                resp
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthResponse>> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nguoi dung khong ton tai"));
        AuthResponse resp = AuthResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .membershipTier(user.getMembershipTier())
                .membershipExpiry(user.getMembershipExpiry())
                .walletBalance(walletService.getBalanceForUser(user.getId()))
                .build();
        return ResponseEntity.ok(ApiResponse.success(resp));
    }
}
