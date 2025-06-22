package org.sample.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class TicketService {

    Map<Integer, String> stations = new HashMap<>();

    public TicketService() {
        stations.put(1, "London");
        stations.put(2, "Paris");
        stations.put(3, "Munich");
        stations.put(4, "Madrid");
        stations.put(5, "Lisbon");
    }
    public Map<Integer, String> getStations() {
    return stations;
    }
    public float tripCost(int startStation, int endStation, int passengerCount) {
        float baseCost = Math.abs((endStation - startStation) * 100);
        return baseCost * passengerCount;
    }
}
