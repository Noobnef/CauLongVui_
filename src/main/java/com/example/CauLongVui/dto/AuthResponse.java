package com.example.CauLongVui.dto;

import com.example.CauLongVui.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private User.Role role;
    private com.example.CauLongVui.entity.MembershipTier membershipTier;
    private java.time.LocalDateTime membershipExpiry;
    private BigDecimal walletBalance;
    private String message;
}
