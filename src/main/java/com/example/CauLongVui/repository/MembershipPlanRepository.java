package com.example.CauLongVui.repository;

import com.example.CauLongVui.entity.MembershipPlan;
import com.example.CauLongVui.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    Optional<MembershipPlan> findByTier(MembershipTier tier);
}
