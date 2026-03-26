package com.example.CauLongVui.controller;

import com.example.CauLongVui.dto.BookingHoldRequest;
import com.example.CauLongVui.dto.BookingHoldResponse;
import com.example.CauLongVui.dto.SlotStateDTO;
import com.example.CauLongVui.service.BookingHoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingHoldController {

    private final BookingHoldService bookingHoldService;

    /**
     * SSE endpoint to stream slot availability updates.
     */
    @GetMapping(value = "/availability/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<SlotStateDTO>> streamUpdates() {
        return bookingHoldService.streamSlotUpdates()
                .map(state -> ServerSentEvent.<SlotStateDTO>builder()
                        .data(state)
                        .build())
                // Keep-alive every 20 seconds
                .mergeWith(Flux.interval(Duration.ofSeconds(20))
                        .map(i -> ServerSentEvent.<SlotStateDTO>builder().comment("keep-alive").build()));
    }

    /**
     * REST endpoint to hold a slot.
     */
    @PostMapping("/hold")
    public BookingHoldResponse holdSlot(@RequestBody BookingHoldRequest request) {
        return bookingHoldService.holdSlot(request);
    }

    /**
     * REST endpoint to release a slot.
     */
    @PostMapping("/release/{holdId}")
    public void releaseSlot(@PathVariable String holdId) {
        bookingHoldService.releaseSlot(holdId);
    }
}
