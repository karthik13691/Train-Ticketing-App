package org.sample.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TicketService {

    Map<Integer, String> stations = new HashMap<>();

    public TicketService() {
        stations.put(1, "Los Angeles");
        stations.put(2, "Las Vegas");
        stations.put(3, "San Francisco");
        stations.put(4, "Portland");
        stations.put(5, "Seattle");
    }
    public Map<Integer, String> getStations() {
    return stations;
    }
    public float tripCost(int startStation, int endStation, int passengerCount) {
        float baseCost = Math.abs((endStation - startStation) * 100);
        return baseCost * passengerCount;
    }

}


