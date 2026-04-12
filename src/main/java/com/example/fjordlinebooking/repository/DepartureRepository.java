package com.example.fjordlinebooking.repository;

import com.example.fjordlinebooking.model.Departure;
import com.example.fjordlinebooking.model.Leg;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DepartureRepository {
    private final Map<String, Departure> departures = new ConcurrentHashMap<>();

    @PostConstruct
    public void seed() {
        List<Leg> legs1 = new ArrayList<>(List.of(
                new Leg("Bergen", "Stavanger", 100, 0),
                new Leg("Stavanger", "Hirtshals", 100, 0),
                new Leg("Hirtshals", "Kristiansand", 100, 0)
        ));
        departures.put("FL101", new Departure("FL101", "Vestland Route", legs1));

        List<Leg> legs2 = new ArrayList<>(List.of(
                new Leg("Bergen", "Stavanger", 50, 0),
                new Leg("Stavanger", "Hirtshals", 50, 0),
                new Leg("Hirtshals", "Kristiansand", 50, 0)
        ));
        departures.put("FL102", new Departure("FL102", "Express Route", legs2));
    }

    public Optional<Departure> findById(String id) { return Optional.ofNullable(departures.get(id)); }

    public Collection<Departure> findAll() { return departures.values(); }
}