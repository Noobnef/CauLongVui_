package com.example.CauLongVui.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawal_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 100)
    private String bankName;

    @Column(nullable = false, length = 50)
    private String accountNumber;

    @Column(nullable = false, length = 100)
    private String accountHolder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WithdrawalStatus status;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum WithdrawalStatus {
        PENDING, APPROVED, REJECTED
    }
}
