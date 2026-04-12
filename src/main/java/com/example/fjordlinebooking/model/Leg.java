package com.example.fjordlinebooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Leg {
    private String fromPort;
    private String toPort;
    private int capacity;
    private int occupied;

    public boolean hasSpace(int count) {
        return (occupied + count) <= capacity;
    }

    public void addPassengers(int count) {
        this.occupied += count;
    }
}
