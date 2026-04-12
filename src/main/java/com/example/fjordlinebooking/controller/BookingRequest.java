package com.example.fjordlinebooking.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private String from;
    private String to;
    private List<String> passengerNames;
    private boolean hasVehicle;
}
