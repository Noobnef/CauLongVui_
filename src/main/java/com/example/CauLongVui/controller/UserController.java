package com.example.CauLongVui.controller;

import com.example.CauLongVui.dto.ApiResponse;
import com.example.CauLongVui.dto.AuthResponse;
import com.example.CauLongVui.entity.User;
import com.example.CauLongVui.repository.UserRepository;
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

    // GET /api/users — Lấy tất cả người dùng
    @GetMapping
    public ResponseEntity<ApiResponse<List<AuthResponse>>> getAllUsers() {
        List<AuthResponse> users = userRepository.findAll().stream()
                .map(u -> AuthResponse.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // PATCH /api/users/{id}/role — Cập nhật quyền người dùng
    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<AuthResponse>> updateRole(
            @PathVariable Long id,
            @RequestParam String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        user.setRole(User.Role.valueOf(role.toUpperCase()));
        userRepository.save(user);
        AuthResponse resp = AuthResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(ApiResponse.success("Cập nhật quyền thành công", resp));
    }

    // PATCH /api/users/{id}/active — Bật/tắt tài khoản
    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<AuthResponse>> toggleActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        user.setActive(active);
        userRepository.save(user);
        AuthResponse resp = AuthResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(ApiResponse.success(
                active ? "Đã kích hoạt tài khoản" : "Đã vô hiệu hóa tài khoản", resp));
    }
}
