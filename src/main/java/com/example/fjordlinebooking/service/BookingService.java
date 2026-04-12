package com.example.fjordlinebooking.service;

import com.example.fjordlinebooking.model.Booking;
import com.example.fjordlinebooking.model.Departure;
import com.example.fjordlinebooking.model.Leg;
import com.example.fjordlinebooking.model.Passenger;
import com.example.fjordlinebooking.repository.DepartureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private DepartureRepository repository;

    private final Map<String, Booking> allBookings = new ConcurrentHashMap<>();

    public synchronized Booking createBooking(String departureId, String from, String to, List<String> passengerNames, boolean hasVehicle) {
        logger.info("Creating booking for departure {} from {} to {}", departureId, from, to);

        Departure departure = repository.findById(departureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Departure not found"));

        List<Leg> relevantLegs = findRelevantLegs(departure, from, to);

        int totalLoad = passengerNames.size() + (hasVehicle ? 5 : 0);

        for (Leg leg : relevantLegs) {
            if (!leg.hasSpace(totalLoad)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Not enough capacity between " + leg.getFromPort() + " and " + leg.getToPort()
                );
            }
        }

        relevantLegs.forEach(leg -> leg.addPassengers(totalLoad));

        String bookingId = UUID.randomUUID().toString().substring(0, 8);

        List<Passenger> passengers = passengerNames.stream()
                .map(Passenger::new)
                .collect(Collectors.toList());

        Booking newBooking = new Booking(bookingId, departureId, from, to, passengers, hasVehicle);

        allBookings.put(bookingId, newBooking);

        return newBooking;
    }

    public synchronized void cancelBooking(String departureId, String bookingId) {
        Booking booking = allBookings.get(bookingId);

        if (booking == null || !booking.getDepartureId().equals(departureId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Booking with ID " + bookingId + " not found"
            );
        }

        Departure departure = repository.findById(departureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Departure data missing"
                ));

        List<Leg> relevantLegs = findRelevantLegs(
                departure,
                booking.getFromPort(),
                booking.getToPort()
        );

        int loadToRelease = booking.getPassengerCount() + (booking.isHasVehicle() ? 5 : 0);

        logger.info("Cancelling booking {}. Releasing {} capacity units.", bookingId, loadToRelease);

        relevantLegs.forEach(leg -> leg.addPassengers(-loadToRelease));

        allBookings.remove(bookingId);
    }

    public List<Passenger> getManifest(String departureId) {
        return allBookings.values().stream()
                .filter(b -> b.getDepartureId().equals(departureId))
                .flatMap(b -> b.getPassengers().stream())
                .collect(Collectors.toList());
    }

    public void clearAllBookings() {
        allBookings.clear();
        repository.findAll().forEach(departure -> {
            departure.getLegs().forEach(leg -> leg.setOccupied(0));
        });
    }

    private List<Leg> findRelevantLegs(Departure departure, String from, String to) {
        List<Leg> allLegs = departure.getLegs();
        int startIndex = -1;
        int endIndex = -1;

        for (int i = 0; i < allLegs.size(); i++) {
            if (allLegs.get(i).getFromPort().equalsIgnoreCase(from)) startIndex = i;
            if (allLegs.get(i).getToPort().equalsIgnoreCase(to)) endIndex = i;
        }

        if (startIndex == -1 || endIndex == -1 || startIndex > endIndex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid travel route for this departure."
            );
        }

        return allLegs.subList(startIndex, endIndex + 1);
    }
}