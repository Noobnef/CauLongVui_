package com.example.CauLongVui.repository;

import com.example.CauLongVui.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);

    Optional<WalletTransaction> findFirstByReferenceIdAndTypeOrderByCreatedAtDesc(
            String referenceId,
            WalletTransaction.TransactionType type
    );
}
