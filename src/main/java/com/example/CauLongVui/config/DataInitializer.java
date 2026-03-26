package com.example.CauLongVui.config;

import com.example.CauLongVui.entity.Court;
import com.example.CauLongVui.entity.Court.CourtStatus;
import com.example.CauLongVui.entity.User;
import com.example.CauLongVui.entity.User.Role;
import com.example.CauLongVui.repository.CourtRepository;
import com.example.CauLongVui.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.example.CauLongVui.repository.MembershipPlanRepository planRepository;

    @Override
    public void run(String... args) {

        // 1. Tao tai khoan Admin mac dinh neu chua co
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = User.builder()
                    .fullName("Quan Tri Vien")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin@123"))
                    .phone("0999888777")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin account created: admin@gmail.com");
        } else {
            log.info("Admin account already exists.");
        }

        // 2. Tao du lieu san mau neu chua co
        if (courtRepository.count() == 0) {
            log.info("No courts found. Seeding sample courts...");

            List<Court> courts = List.of(
                    Court.builder()
                            .name("San A1")
                            .description("San tieu chuan tang 1 - Anh sang tot, thong thoang")
                            .pricePerHour(90000.0)
                            .status(CourtStatus.AVAILABLE)
                            .build(),
                    Court.builder()
                            .name("San A2")
                            .description("San tieu chuan tang 1 - Gan cua ra vao")
                            .pricePerHour(90000.0)
                            .status(CourtStatus.AVAILABLE)
                            .build(),
                    Court.builder()
                            .name("San A3")
                            .description("San tieu chuan tang 1 - San goc yen tinh")
                            .pricePerHour(90000.0)
                            .status(CourtStatus.AVAILABLE)
                            .build(),
                    Court.builder()
                            .name("San B1")
                            .description("San VIP tang 2 - Dieu hoa, ghe ngoi thoai mai")
                            .pricePerHour(150000.0)
                            .status(CourtStatus.AVAILABLE)
                            .build(),
                    Court.builder()
                            .name("San B2")
                            .description("San VIP tang 2 - View dep nhin ra san ngoai")
                            .pricePerHour(150000.0)
                            .status(CourtStatus.AVAILABLE)
                            .build());

            courtRepository.saveAll(courts);
            log.info("Seeded {} courts successfully.", courts.size());
        } else {
            log.info("Courts already exist ({}). Skipping seed.", courtRepository.count());
        }

        // 3. Tao goi membership mac dinh
        if (planRepository.count() == 0) {
            log.info("No membership plans found. Seeding default plans...");

            List<com.example.CauLongVui.entity.MembershipPlan> plans = List.of(
                    com.example.CauLongVui.entity.MembershipPlan.builder()
                            .name("Hội viên Thường")
                            .tier(com.example.CauLongVui.entity.MembershipTier.NORMAL)
                            .price(0L)
                            .durationInDays(36500)
                            .description("Gói mặc định cho người dùng mới.")
                            .build(),
                    com.example.CauLongVui.entity.MembershipPlan.builder()
                            .name("Hội viên Pro")
                            .tier(com.example.CauLongVui.entity.MembershipTier.PRO)
                            .price(50000L)
                            .durationInDays(30)
                            .description("Nhiều ưu đãi và giảm giá đặt sân.")
                            .build(),
                    com.example.CauLongVui.entity.MembershipPlan.builder()
                            .name("Hội viên VIP")
                            .tier(com.example.CauLongVui.entity.MembershipTier.VIP)
                            .price(150000L)
                            .durationInDays(30)
                            .description("Ưu tiên đặt sân và giảm giá tối đa.")
                            .build());

            planRepository.saveAll(plans);
            log.info("Seeded {} membership plans successfully.", plans.size());
        }
    }
}
