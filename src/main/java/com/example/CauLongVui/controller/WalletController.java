package com.example.CauLongVui.controller;

import com.example.CauLongVui.dto.*;
import com.example.CauLongVui.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<WalletDTO>> getWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(walletService.getWalletByUserId(userId)));
    }

    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<ApiResponse<PaginationResponse<WalletTransactionDTO>>> getTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(walletService.getTransactions(userId, page, size)));
    }

    @GetMapping("/user/{userId}/withdrawals")
    public ResponseEntity<ApiResponse<List<WithdrawalRequestDTO>>> getWithdrawals(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(walletService.getWithdrawalRequestsByUserId(userId)));
    }

    @PostMapping("/topup")
    public ResponseEntity<ApiResponse<Map<String, String>>> topUp(@RequestBody TopUpRequest request) throws Exception {
        return ResponseEntity.ok(ApiResponse.success("Tao link nap tien thanh cong", walletService.initiateTopUp(request)));
    }

    @PostMapping("/bookings/{bookingId}/pay")
    public ResponseEntity<ApiResponse<BookingDTO>> payBookingWithWallet(
            @PathVariable Long bookingId,
            @RequestBody WalletPaymentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Thanh toan bang vi thanh cong",
                walletService.payBookingWithWallet(bookingId, request.getUserId())
        ));
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<ApiResponse<WithdrawalRequestDTO>> createWithdrawal(
            @RequestBody CreateWithdrawalRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tao yeu cau rut tien thanh cong", walletService.createWithdrawal(request)));
    }
}
