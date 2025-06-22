package org.sample.model;

import lombok.Data;
import org.sample.service.TicketService;
import java.util.Scanner;


@Data
public class Ticket {
    private String startingPoint;
    private String destinationPoint;
    private int startingPointInt;
    private int destinationPointInt;
    private float tripCost;
    private int passengerCount;
}