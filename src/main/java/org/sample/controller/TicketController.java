package org.sample.controller;

import org.sample.model.Ticket;
import org.sample.service.TicketService;
import org.sample.service.PdfService;
import org.sample.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Controller
public class TicketController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    private final TicketService ticketService;
    private final PdfService pdfService;
    private final EmailService emailService;

    @Autowired
    public TicketController(TicketService ticketService, PdfService pdfService, EmailService emailService) {
        this.ticketService = ticketService;
        this.pdfService = pdfService;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String showBookingForm(Model model) {
        model.addAttribute("stations", ticketService.getStations());
        model.addAttribute("minDate", LocalDate.now());
        return "booking";
    }

    @PostMapping("/book")
    public String bookTicket(@RequestParam("startStation") int startStation,
                             @RequestParam("endStation") int endStation,
                             @RequestParam("passengerCount") int passengerCount,
                             @RequestParam("travelDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate travelDate,
                             @RequestParam("travelTime") String travelTime,
                             @RequestParam("passengerName") String passengerName,
                             @RequestParam(value = "email", required = false) String email,
                             Model model) {
        try {
            logger.info("Processing booking for passenger: {}", passengerName);

            float cost = ticketService.tripCost(startStation, endStation, passengerCount);
            String startStationName = ticketService.getStations().get(startStation);
            String endStationName = ticketService.getStations().get(endStation);

            // Create ticket object
            Ticket ticket = new Ticket();
            ticket.setPassengerName(passengerName);
            ticket.setStartingPoint(startStationName);
            ticket.setDestinationPoint(endStationName);
            ticket.setStartingPointInt(startStation);
            ticket.setDestinationPointInt(endStation);
            ticket.setPassengerCount(passengerCount);
            ticket.setTripCost(cost);
            ticket.setTravelDate(travelDate);
            ticket.setTravelTime(travelTime);

            // Generate PDF
            String pdfPath = pdfService.generateTicketPdf(ticket);
            logger.info("PDF generated: {}", pdfPath);

            // Send email only if provided
            boolean emailSent = false;
            if (email != null && !email.trim().isEmpty()) {
                try {
                    emailService.sendTicketConfirmation(email, pdfPath, ticket);
                    emailSent = true;
                    logger.info("Confirmation email sent to: {}", email);
                } catch (Exception e) {
                    logger.warn("Failed to send email confirmation: {}", e.getMessage());
                    // Continue without email
                }
            }

            // Add attributes to model
            model.addAttribute("passengerName", passengerName);
            model.addAttribute("cost", cost);
            model.addAttribute("startStation", startStationName);
            model.addAttribute("endStation", endStationName);
            model.addAttribute("passengerCount", passengerCount);
            model.addAttribute("travelDate", travelDate);
            model.addAttribute("travelTime", travelTime);
            model.addAttribute("pdfPath", pdfPath);
            model.addAttribute("emailSent", emailSent);
            model.addAttribute("email", email);

            logger.info("Booking completed successfully for passenger: {}", passengerName);
            return "confirmation";
        } catch (Exception e) {
            logger.error("Error during booking for passenger: {}", passengerName, e);
            model.addAttribute("error", "Booking failed: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/download-ticket/{fileName:.+}")
    public ResponseEntity<Resource> downloadTicket(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("tickets").resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                logger.info("Downloading ticket: {}", fileName);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                logger.error("File not found: {}", fileName);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error downloading ticket: {}", fileName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParams(MissingServletRequestParameterException ex, Model model) {
        String paramName = ex.getParameterName();
        model.addAttribute("error", "Required parameter '" + paramName + "' is missing");
        return "error";
    }
}
