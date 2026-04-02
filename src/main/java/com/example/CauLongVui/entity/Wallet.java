package com.example.CauLongVui.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets", uniqueConstraints = {
        @UniqueConstraint(name = "uk_wallet_user", columnNames = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    private static final BigDecimal ZERO = new BigDecimal("0.00");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance = ZERO;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        normalizeBalance();
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
    }

    @PreUpdate
    public void preUpdate() {
        normalizeBalance();
        updatedAt = LocalDateTime.now();
    }

    private void normalizeBalance() {
        if (balance == null) {
            balance = ZERO;
        }
        balance = balance.setScale(2, RoundingMode.HALF_UP);
    }
}
