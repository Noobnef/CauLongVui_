package com.example.CauLongVui.controller;

import com.example.CauLongVui.dto.ApiResponse;
import com.example.CauLongVui.entity.MembershipPlan;
import com.example.CauLongVui.service.MembershipService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<MembershipPlan>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.success(membershipService.getAllPlans()));
    }

    @PostMapping("/purchase/{planId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> purchasePlan(
            @PathVariable Long planId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "vnpay") String method,
            HttpServletRequest request) throws Exception {
        String paymentUrl = membershipService.initiatePurchase(planId, userId, method, request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("paymentUrl", paymentUrl)));
    }

    @PostMapping("/momo-callback")
    public ResponseEntity<Void> momoCallback(@RequestBody Map<String, Object> payload) {
        // Simple implementation for now, should verify signature
        String orderId = (String) payload.get("orderId");
        Integer resultCode = (Integer) payload.get("resultCode");
        
        if (orderId != null && orderId.startsWith("MB-")) {
            String[] parts = orderId.split("-");
            if (parts.length >= 2) {
                Long subId = Long.parseLong(parts[1]);
                if (resultCode != null && resultCode == 0) {
                    membershipService.completePurchase(subId);
                } else {
                    membershipService.cancelPurchase(subId);
                }
            }
        }
        return ResponseEntity.ok().build();
    }
}
