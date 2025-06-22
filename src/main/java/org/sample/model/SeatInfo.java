package org.sample.model;

import lombok.Data;

@Data
public class SeatInfo {
    private String seatNumber;
    private String coach;
    private boolean isAvailable;
}
