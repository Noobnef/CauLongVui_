package com.example.CauLongVui.service;

import com.example.CauLongVui.dto.AuthResponse;
import com.example.CauLongVui.dto.LoginRequest;
import com.example.CauLongVui.dto.RegisterRequest;
import com.example.CauLongVui.dto.UpdateProfileRequest;
import com.example.CauLongVui.entity.User;
import com.example.CauLongVui.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email da duoc su dung: " + req.getEmail());
        }

        User user = User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(User.Role.CUSTOMER)
                .build();

        userRepository.save(user);
        walletService.createWalletForUser(user);
        return toResponse(user, "Dang ky thanh cong! Chao mung ban den voi Cau Long Vui");
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoac mat khau khong chinh xac"));

        if (!user.getActive()) {
            throw new IllegalArgumentException("Tai khoan cua ban da bi vo hieu hoa");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Email hoac mat khau khong chinh xac");
        }

        return toResponse(user, "Dang nhap thanh cong! Xin chao, " + user.getFullName());
    }

    public AuthResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nguoi dung khong ton tai"));
        return toResponse(user, null);
    }

    public AuthResponse updateProfile(UpdateProfileRequest req) {
        User user = userRepository.findById(req.getId())
                .orElseThrow(() -> new IllegalArgumentException("Nguoi dung khong ton tai"));

        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            user.setFullName(req.getFullName().trim());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone().trim().isEmpty() ? null : req.getPhone().trim());
        }
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (req.getCurrentPassword() == null
                    || !passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Mat khau hien tai khong dung");
            }
            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }

        userRepository.save(user);
        return toResponse(user, "Cap nhat thong tin thanh cong");
    }

    private AuthResponse toResponse(User user, String message) {
        return AuthResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .membershipTier(user.getMembershipTier())
                .membershipExpiry(user.getMembershipExpiry())
                .walletBalance(walletService.getBalanceForUser(user.getId()))
                .message(message)
                .build();
    }
}
