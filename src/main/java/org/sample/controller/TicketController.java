package org.sample.controller;

import org.sample.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public String showBookingForm(Model model) {
        model.addAttribute("stations", ticketService.getStations());
        return "booking";
    }

    // Add this method
    @PostMapping("/book")
    public String bookTicket(@RequestParam("startStation") int startStation,
                             @RequestParam("endStation") int endStation,
                             @RequestParam ("passengerCount") int passengerCount,
                             Model model) {
        float cost = ticketService.tripCost(startStation, endStation, passengerCount);
        String startStationName = ticketService.getStations().get(startStation);
        String endStationName = ticketService.getStations().get(endStation);

        model.addAttribute("cost", cost);
        model.addAttribute("startStation", startStationName);
        model.addAttribute("endStation", endStationName);
        return "confirmation";
    }
}
