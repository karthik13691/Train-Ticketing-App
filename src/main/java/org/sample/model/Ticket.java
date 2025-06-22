package org.sample.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Ticket {
    private String passengerName;
    private String startingPoint;
    private String destinationPoint;
    private int startingPointInt;
    private int destinationPointInt;
    private int passengerCount;
    private float tripCost;
    private LocalDate travelDate;
    private String travelTime;
}
