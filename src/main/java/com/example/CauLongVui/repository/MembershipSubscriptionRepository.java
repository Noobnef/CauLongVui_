package com.example.CauLongVui.repository;

import com.example.CauLongVui.entity.MembershipSubscription;
import com.example.CauLongVui.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MembershipSubscriptionRepository extends JpaRepository<MembershipSubscription, Long> {
    List<MembershipSubscription> findByUser(User user);
    Optional<MembershipSubscription> findByVnpayTxnRef(String vnpayTxnRef);
}
