package com.example.CauLongVui.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWithdrawalRequest {
    private Long userId;
    private BigDecimal amount;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
}
