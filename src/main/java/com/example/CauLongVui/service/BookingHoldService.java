package com.example.CauLongVui.service;

import com.example.CauLongVui.dto.BookingHoldRequest;
import com.example.CauLongVui.dto.BookingHoldResponse;
import com.example.CauLongVui.dto.SlotStateDTO;
import com.example.CauLongVui.entity.Booking;
import com.example.CauLongVui.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingHoldService {

    private final BookingRepository bookingRepository;

    // Key: "courtId:date:startTime:endTime" → HoldEntry
    private final ConcurrentHashMap<String, HoldEntry> holdMap = new ConcurrentHashMap<>();

    // Sink to broadcast slot state changes to all subscribers
    private final Sinks.Many<SlotStateDTO> slotStateSink = Sinks.many().multicast().onBackpressureBuffer();

    // Hold TTL: 5 minutes
    private static final long HOLD_TTL_MILLIS = 5 * 60 * 1000;

    public BookingHoldService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Attempt to hold a timeslot for a user.
     */
    public synchronized BookingHoldResponse holdSlot(BookingHoldRequest request) {
        String slotKey = buildSlotKey(request.getCourtId(), request.getBookingDate(),
                request.getStartTime(), request.getEndTime());

        // 1. Check if the slot is already booked in the database
        List<Booking> dayBookings = bookingRepository.findByCourtIdAndBookingDate(
                request.getCourtId(), request.getBookingDate());

        boolean isBookedInDb = dayBookings.stream()
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
                .anyMatch(b -> request.getStartTime().isBefore(b.getEndTime())
                        && request.getEndTime().isAfter(b.getStartTime()));

        if (isBookedInDb) {
            return BookingHoldResponse.builder()
                    .courtId(request.getCourtId())
                    .bookingDate(request.getBookingDate())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .userId(request.getUserId())
                    .success(false)
                    .message("Khung giờ này đã được đặt.")
                    .build();
        }

        // 2. Check if the slot is already held by another user
        HoldEntry existingHold = holdMap.get(slotKey);
        if (existingHold != null && !existingHold.isExpired()) {
            // If held by the same user, extend the hold
            if (existingHold.getUserId().equals(request.getUserId())) {
                existingHold.setCreatedAt(Instant.now());
                return BookingHoldResponse.builder()
                        .holdId(existingHold.getHoldId())
                        .courtId(request.getCourtId())
                        .bookingDate(request.getBookingDate())
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .userId(request.getUserId())
                        .success(true)
                        .message("Đã gia hạn giữ chỗ.")
                        .build();
            }
            // Held by a different user
            return BookingHoldResponse.builder()
                    .courtId(request.getCourtId())
                    .bookingDate(request.getBookingDate())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .userId(request.getUserId())
                    .success(false)
                    .message("Khung giờ đang được người khác giữ chỗ. Vui lòng thử lại sau.")
                    .build();
        }

        // 3. Create a new hold
        String holdId = UUID.randomUUID().toString();
        HoldEntry newHold = new HoldEntry(holdId, request.getUserId(),
                request.getCourtId(), request.getBookingDate(),
                request.getStartTime(), request.getEndTime(), Instant.now());
        holdMap.put(slotKey, newHold);

        // Broadcast state change
        broadcastSlotState(request.getCourtId(), request.getBookingDate(),
                request.getStartTime(), request.getEndTime(),
                SlotStateDTO.SlotStatus.HELD, request.getUserId(), holdId);

        log.info("Slot held: {} by user {}", slotKey, request.getUserId());

        return BookingHoldResponse.builder()
                .holdId(holdId)
                .courtId(request.getCourtId())
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .userId(request.getUserId())
                .success(true)
                .message("Giữ chỗ thành công. Vui lòng hoàn tất đặt sân trong 5 phút.")
                .build();
    }

    /**
     * Release a held slot.
     */
    public void releaseSlot(String holdId) {
        holdMap.values().removeIf(entry -> {
            if (entry.getHoldId().equals(holdId)) {
                broadcastSlotState(entry.getCourtId(), entry.getBookingDate(),
                        entry.getStartTime(), entry.getEndTime(),
                        SlotStateDTO.SlotStatus.AVAILABLE, null, null);
                log.info("Slot released: holdId={}", holdId);
                return true;
            }
            return false;
        });
    }

    /**
     * Release all holds by a user (e.g., on disconnect).
     */
    public void releaseAllByUser(Long userId) {
        holdMap.values().removeIf(entry -> {
            if (entry.getUserId().equals(userId)) {
                broadcastSlotState(entry.getCourtId(), entry.getBookingDate(),
                        entry.getStartTime(), entry.getEndTime(),
                        SlotStateDTO.SlotStatus.AVAILABLE, null, null);
                log.info("Slot released on disconnect: user={}", userId);
                return true;
            }
            return false;
        });
    }

    /**
     * Confirm a hold during booking creation. Returns true if the holdId is valid.
     */
    public boolean confirmHold(String holdId) {
        HoldEntry entry = holdMap.values().stream()
                .filter(e -> e.getHoldId().equals(holdId))
                .findFirst()
                .orElse(null);

        if (entry == null || entry.isExpired()) {
            return false;
        }

        // Remove from hold map — now it's a real booking
        String slotKey = buildSlotKey(entry.getCourtId(), entry.getBookingDate(),
                entry.getStartTime(), entry.getEndTime());
        holdMap.remove(slotKey);

        broadcastSlotState(entry.getCourtId(), entry.getBookingDate(),
                entry.getStartTime(), entry.getEndTime(),
                SlotStateDTO.SlotStatus.BOOKED, entry.getUserId(), null);

        return true;
    }

    /**
     * Get all active holds for a court on a given date.
     */
    public List<SlotStateDTO> getHeldSlots(Long courtId, LocalDate date) {
        return holdMap.values().stream()
                .filter(e -> e.getCourtId().equals(courtId)
                        && e.getBookingDate().equals(date)
                        && !e.isExpired())
                .map(e -> SlotStateDTO.builder()
                        .courtId(e.getCourtId())
                        .bookingDate(e.getBookingDate())
                        .startTime(e.getStartTime())
                        .endTime(e.getEndTime())
                        .status(SlotStateDTO.SlotStatus.HELD)
                        .heldByUserId(e.getUserId())
                        .holdId(e.getHoldId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Subscribe to the slot state change stream.
     */
    public Flux<SlotStateDTO> streamSlotUpdates() {
        return slotStateSink.asFlux();
    }

    /**
     * Scheduled task: clean up expired holds every 30 seconds.
     */
    @Scheduled(fixedRate = 30_000)
    public void cleanupExpiredHolds() {
        holdMap.values().removeIf(entry -> {
            if (entry.isExpired()) {
                broadcastSlotState(entry.getCourtId(), entry.getBookingDate(),
                        entry.getStartTime(), entry.getEndTime(),
                        SlotStateDTO.SlotStatus.AVAILABLE, null, null);
                log.info("Expired hold removed: holdId={}, user={}", entry.getHoldId(), entry.getUserId());
                return true;
            }
            return false;
        });
    }

    // === Private Helpers ===

    private String buildSlotKey(Long courtId, LocalDate date, LocalTime start, LocalTime end) {
        return courtId + ":" + date + ":" + start + ":" + end;
    }

    private void broadcastSlotState(Long courtId, LocalDate date,
                                     LocalTime start, LocalTime end,
                                     SlotStateDTO.SlotStatus status,
                                     Long userId, String holdId) {
        SlotStateDTO state = SlotStateDTO.builder()
                .courtId(courtId)
                .bookingDate(date)
                .startTime(start)
                .endTime(end)
                .status(status)
                .heldByUserId(userId)
                .holdId(holdId)
                .build();
        slotStateSink.tryEmitNext(state);
    }

    // === Inner class for hold entries ===

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class HoldEntry {
        private String holdId;
        private Long userId;
        private Long courtId;
        private LocalDate bookingDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Instant createdAt;

        public boolean isExpired() {
            return Instant.now().toEpochMilli() - createdAt.toEpochMilli() > HOLD_TTL_MILLIS;
        }
    }
}
