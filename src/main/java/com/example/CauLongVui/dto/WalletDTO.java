package com.example.CauLongVui.dto;

import com.example.CauLongVui.entity.Wallet;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletDTO {
    private Long id;
    private Long userId;
    private BigDecimal balance;

    public static WalletDTO fromEntity(Wallet wallet) {
        return WalletDTO.builder()
                .id(wallet.getId())
                .userId(wallet.getUser().getId())
                .balance(wallet.getBalance())
                .build();
    }
}
