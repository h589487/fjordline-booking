package com.example.fjordlinebooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private String bookingId;
    private String departureId;
    private String fromPort;
    private String toPort;
    private List<Passenger> passengers;
    private boolean hasVehicle;

    public int getPassengerCount() {
        return passengers.size();
    }
}