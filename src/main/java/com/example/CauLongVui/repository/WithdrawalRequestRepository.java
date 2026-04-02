package com.example.CauLongVui.repository;

import com.example.CauLongVui.entity.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {

    List<WithdrawalRequest> findAllByOrderByCreatedAtDesc();

    List<WithdrawalRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
}
