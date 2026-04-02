package com.example.CauLongVui.dto;

import com.example.CauLongVui.entity.WithdrawalRequest;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalRequestDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private BigDecimal amount;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private WithdrawalRequest.WithdrawalStatus status;
    private LocalDateTime createdAt;

    public static WithdrawalRequestDTO fromEntity(WithdrawalRequest request) {
        return WithdrawalRequestDTO.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .userName(request.getUser().getFullName())
                .userEmail(request.getUser().getEmail())
                .userPhone(request.getUser().getPhone())
                .amount(request.getAmount())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolder(request.getAccountHolder())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
