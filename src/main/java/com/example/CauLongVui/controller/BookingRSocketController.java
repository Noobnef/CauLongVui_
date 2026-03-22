package com.example.CauLongVui.controller;

import com.example.CauLongVui.dto.BookingHoldRequest;
import com.example.CauLongVui.dto.BookingHoldResponse;
import com.example.CauLongVui.dto.SlotStateDTO;
import com.example.CauLongVui.service.BookingHoldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookingRSocketController {

    private final BookingHoldService bookingHoldService;

    /**
     * Request-Response: Client requests to hold a timeslot.
     * Route: "booking.hold"
     */
    @MessageMapping("booking.hold")
    public Mono<BookingHoldResponse> holdSlot(BookingHoldRequest request) {
        log.info("RSocket hold request: court={}, date={}, time={}-{}",
                request.getCourtId(), request.getBookingDate(),
                request.getStartTime(), request.getEndTime());
        BookingHoldResponse response = bookingHoldService.holdSlot(request);
        return Mono.just(response);
    }

    /**
     * Fire-and-Forget: Client releases a held slot.
     * Route: "booking.release"
     */
    @MessageMapping("booking.release")
    public Mono<Void> releaseSlot(String holdId) {
        log.info("RSocket release request: holdId={}", holdId);
        bookingHoldService.releaseSlot(holdId);
        return Mono.empty();
    }

    /**
     * Request-Stream: Client subscribes to real-time slot state changes.
     * Route: "booking.availability.stream"
     */
    @MessageMapping("booking.availability.stream")
    public Flux<SlotStateDTO> streamAvailability(String courtIdAndDate) {
        log.info("RSocket stream subscription: {}", courtIdAndDate);
        return bookingHoldService.streamSlotUpdates();
    }
}
