package com.example.CauLongVui.service;

import com.example.CauLongVui.config.VNPayConfig;
import com.example.CauLongVui.entity.MembershipPlan;
import com.example.CauLongVui.entity.MembershipSubscription;
import com.example.CauLongVui.entity.User;
import com.example.CauLongVui.repository.MembershipPlanRepository;
import com.example.CauLongVui.repository.MembershipSubscriptionRepository;
import com.example.CauLongVui.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipService {

    private final MembershipPlanRepository planRepository;
    private final MembershipSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final VNPayService vnPayService;
    private final MomoService momoService;

    public List<MembershipPlan> getAllPlans() {
        return planRepository.findAll();
    }

    @Transactional
    public String initiatePurchase(Long planId, Long userId, String method, HttpServletRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        // Create a pending subscription
        MembershipSubscription subscription = MembershipSubscription.builder()
                .user(user)
                .plan(plan)
                .purchaseDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(plan.getDurationInDays()))
                .status(MembershipSubscription.SubscriptionStatus.PENDING)
                .build();

        subscription = subscriptionRepository.save(subscription);

        String orderId = "MB-" + subscription.getId();
        String orderInfo = "Mua gói " + plan.getName() + " cho user " + user.getEmail();
        
        String paymentUrl;
        if ("momo".equalsIgnoreCase(method)) {
            paymentUrl = momoService.createPayment(plan.getPrice(), orderId, orderInfo).getPayUrl();
        } else {
            String ipAddress = VNPayConfig.getIpAddress(request);
            paymentUrl = vnPayService.createPaymentUrl(plan.getPrice(), orderInfo, orderId, ipAddress);
        }

        // Update subscription with txn ref (using orderId as txn ref for now)
        subscription.setVnpayTxnRef(orderId);
        subscriptionRepository.save(subscription);

        return paymentUrl;
    }

    @Transactional
    public void completePurchase(Long subscriptionId) {
        MembershipSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if (subscription.getStatus() == MembershipSubscription.SubscriptionStatus.COMPLETED) {
            return;
        }

        subscription.setStatus(MembershipSubscription.SubscriptionStatus.COMPLETED);
        subscriptionRepository.save(subscription);

        // Update User Membership
        User user = subscription.getUser();
        user.setMembershipTier(subscription.getPlan().getTier());
        
        // If user already has an active membership of the same tier, extend it
        LocalDateTime newExpiry;
        if (user.getMembershipExpiry() != null && user.getMembershipExpiry().isAfter(LocalDateTime.now())) {
            newExpiry = user.getMembershipExpiry().plusDays(subscription.getPlan().getDurationInDays());
        } else {
            newExpiry = LocalDateTime.now().plusDays(subscription.getPlan().getDurationInDays());
        }
        user.setMembershipExpiry(newExpiry);
        userRepository.save(user);

        log.info("Membership updated for user {}: Tier {}, Expiry {}", user.getEmail(), user.getMembershipTier(), user.getMembershipExpiry());
    }

    @Transactional
    public void cancelPurchase(Long subscriptionId) {
        MembershipSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        subscription.setStatus(MembershipSubscription.SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }
}
