package com.example.CauLongVui.dto;

import com.example.CauLongVui.entity.WithdrawalRequest;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalActionRequest {
    private WithdrawalRequest.WithdrawalStatus status;
}
