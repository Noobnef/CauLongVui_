package com.example.CauLongVui.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopUpRequest {
    private Long userId;
    private BigDecimal amount;
}
