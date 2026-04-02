package com.example.CauLongVui.dto;

import com.example.CauLongVui.entity.WalletTransaction;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionDTO {
    private Long id;
    private Long walletId;
    private BigDecimal amount;
    private WalletTransaction.TransactionStatus status;
    private WalletTransaction.TransactionType type;
    private String referenceId;
    private String description;
    private LocalDateTime createdAt;

    public static WalletTransactionDTO fromEntity(WalletTransaction transaction) {
        return WalletTransactionDTO.builder()
                .id(transaction.getId())
                .walletId(transaction.getWallet().getId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .type(transaction.getType())
                .referenceId(transaction.getReferenceId())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
