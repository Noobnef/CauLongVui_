package com.example.CauLongVui.controller;

import com.example.CauLongVui.dto.ApiResponse;
import com.example.CauLongVui.dto.WithdrawalActionRequest;
import com.example.CauLongVui.dto.WithdrawalRequestDTO;
import com.example.CauLongVui.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/wallets")
@RequiredArgsConstructor
public class WalletAdminController {

    private final WalletService walletService;

    @GetMapping("/withdrawals")
    public ResponseEntity<ApiResponse<List<WithdrawalRequestDTO>>> getAllWithdrawals() {
        return ResponseEntity.ok(ApiResponse.success(walletService.getAllWithdrawalRequests()));
    }

    @PatchMapping("/withdrawals/{id}")
    public ResponseEntity<ApiResponse<WithdrawalRequestDTO>> reviewWithdrawal(
            @PathVariable Long id,
            @RequestBody WithdrawalActionRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat yeu cau rut tien thanh cong",
                walletService.reviewWithdrawal(id, request.getStatus())
        ));
    }
}
