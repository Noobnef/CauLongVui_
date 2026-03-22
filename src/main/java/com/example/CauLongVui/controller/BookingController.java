package com.example.CauLongVui.controller;

import com.example.CauLongVui.dto.ApiResponse;
import com.example.CauLongVui.dto.BookingDTO;
import com.example.CauLongVui.entity.Booking;
import com.example.CauLongVui.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // GET /api/bookings/slots?courtId=&date= — lấy các slot đã đặt theo sân và ngày (dùng cho lịch)
    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getBookedSlots(
            @RequestParam(name = "courtId") Long courtId,
            @RequestParam(name = "date") String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Map<String, String>> slots = bookingService.getBookedSlots(courtId, localDate);
        return ResponseEntity.ok(ApiResponse.success(slots));
    }

    // GET /api/bookings — lấy tất cả đặt sân (có thể filter theo courtId hoặc phone)
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookings(
            @RequestParam(name = "courtId", required = false) Long courtId,
            @RequestParam(name = "userId",  required = false) Long userId,
            @RequestParam(name = "phone",   required = false) String phone) {
        List<BookingDTO> bookings;
        if (courtId != null) {
            bookings = bookingService.getBookingsByCourtId(courtId);
        } else if (userId != null) {
            bookings = bookingService.getBookingsByUserId(userId);
        } else if (phone != null && !phone.isBlank()) {
            bookings = bookingService.getBookingsByPhone(phone);
        } else {
            bookings = bookingService.getAllBookings();
        }
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    // GET /api/bookings/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDTO>> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingById(id)));
    }

    // POST /api/bookings — đặt sân mới (optional holdId from RSocket hold)
    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> createBooking(
            @RequestBody BookingDTO bookingDTO,
            @RequestParam(name = "holdId", required = false) String holdId) {
        BookingDTO created = bookingService.createBooking(bookingDTO, holdId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đặt sân thành công", created));
    }

    // PATCH /api/bookings/{id}/status — cập nhật trạng thái
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BookingDTO>> updateStatus(@PathVariable Long id,
                                                                  @RequestParam(name = "status") Booking.BookingStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công",
                bookingService.updateBookingStatus(id, status)));
    }
}
