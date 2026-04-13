package com.example.fjordlinebooking.controller;

import com.example.fjordlinebooking.dto.BookingRequest;
import com.example.fjordlinebooking.model.Booking;
import com.example.fjordlinebooking.model.Departure;
import com.example.fjordlinebooking.model.Passenger;
import com.example.fjordlinebooking.repository.DepartureRepository;
import com.example.fjordlinebooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/departures")
public class DepartureController {

    @Autowired
    private DepartureRepository repository;

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public Collection<Departure> getAllDepartures() {
        return repository.findAll();
    }

    @PostMapping("/{id}/bookings")
    public Booking createBooking(
            @PathVariable String id,
            @RequestBody BookingRequest request) {

        return bookingService.createBooking(
                id,
                request.getFrom(),
                request.getTo(),
                request.getPassengerNames(),
                request.isHasVehicle()
        );
    }

    @GetMapping("/{id}/manifest")
    public List<Passenger> getManifest(@PathVariable String id) {
        return bookingService.getManifest(id);
    }

    @DeleteMapping("/{id}/bookings/{bookingId}")
    public String cancelBooking(
            @PathVariable String id,
            @PathVariable String bookingId) {

        bookingService.cancelBooking(id, bookingId);
        return "Booking " + bookingId + " is canceled.";
    }
}