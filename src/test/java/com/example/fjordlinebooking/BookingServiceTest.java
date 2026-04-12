package com.example.fjordlinebooking;

import com.example.fjordlinebooking.model.Booking;
import com.example.fjordlinebooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService.clearAllBookings();
    }

    @Test
    @DisplayName("Should be able to book and then cancel to free up capacity (Capacity 50)")
    void testBookingFullCircle() {
        String depId = "FL102";

        List<String> largeGroup = IntStream.rangeClosed(1, 45)
                .mapToObj(i -> "Passenger " + i)
                .toList();

        Booking firstBooking = bookingService.createBooking(depId, "Bergen", "Stavanger", largeGroup, false);
        assertEquals(45, firstBooking.getPassengerCount());

        List<String> tooMany = List.of("P1", "P2", "P3", "P4", "P5", "P6");
        assertThrows(ResponseStatusException.class, () -> {
            bookingService.createBooking(depId, "Bergen", "Stavanger", tooMany, false);
        }, "Should throw an error when overbooking total capacity");

        bookingService.cancelBooking(depId, firstBooking.getBookingId());

        assertDoesNotThrow(() -> {
            bookingService.createBooking(depId, "Bergen", "Stavanger", List.of("Ola", "Kari"), false);
        });
    }

    @Test
    @DisplayName("Should fail when attempting to book an invalid route")
    void testInvalidRoute() {
        assertThrows(ResponseStatusException.class, () -> {
            bookingService.createBooking("FL101", "Stavanger", "Bergen", List.of("Invalid"), false);
        });
    }

    @Test
    @DisplayName("Should verify that vehicles consume more capacity than passengers")
    void testVehicleCapacity() {
        String depId = "FL102";

        List<String> group = IntStream.rangeClosed(1, 46)
                .mapToObj(i -> "P" + i)
                .toList();

        assertThrows(ResponseStatusException.class, () -> {
            bookingService.createBooking(depId, "Bergen", "Stavanger", group, true);
        }, "Should fail because passengers + vehicle exceed capacity 50");
    }
}