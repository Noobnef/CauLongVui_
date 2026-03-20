package com.example.CauLongVui.repository;

import com.example.CauLongVui.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCourtId(Long courtId);

    List<Booking> findByBookingDate(LocalDate date);

    // Check for overlapping bookings: same court, same day, overlapping times, not cancelled
    @Query("SELECT b FROM Booking b WHERE b.court.id = :courtId AND b.bookingDate = :date " +
           "AND b.status <> 'CANCELLED' " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findOverlappingBookings(@Param("courtId") Long courtId,
                                          @Param("date") LocalDate date,
                                          @Param("startTime") LocalTime startTime,
                                          @Param("endTime") LocalTime endTime);
}
