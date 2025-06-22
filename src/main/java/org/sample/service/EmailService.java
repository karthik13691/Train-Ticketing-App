package org.sample.service;

import jakarta.mail.internet.MimeMessage;
import org.sample.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    public void sendTicketConfirmation(String to, String ticketPdfPath, Ticket ticket) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("your-email@company.com");
            helper.setTo(to);
            helper.setSubject("Train Ticket Confirmation - " + ticket.getPassengerName());

            String formattedDate = ticket.getTravelDate().format(
                    DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

            String emailContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .header {
                            background-image: url('https://www.travelandleisure.com/thmb/e0AATW8Ph84NmaRqgITvxdgR9a8=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/TAL-header-exterior-venice-simplon-orient-express-TRAINSARNDWRLD1024-39dc0132d52848849a6c326cb751a8a1.jpg');
                            background-size: cover;
                            background-position: center;
                            color: white;
                            text-align: center;
                            padding: 40px;
                            border-radius: 10px 10px 0 0;
                        }
                        .content {
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 0 0 10px 10px;
                            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                        }
                        .detail-row {
                            margin: 10px 0;
                            padding: 10px;
                            background-color: #f8f9fa;
                            border-radius: 5px;
                        }
                        .label {
                            font-weight: bold;
                            color: #495057;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 20px;
                            color: #6c757d;
                            font-style: italic;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Train Ticket Confirmation</h1>
                        </div>
                        <div class="content">
                            <p>Dear %s,</p>
                            <p>Your ticket has been confirmed! Here are your journey details:</p>
                            
                            <div class="detail-row">
                                <span class="label">From:</span> %s
                            </div>
                            <div class="detail-row">
                                <span class="label">To:</span> %s
                            </div>
                            <div class="detail-row">
                                <span class="label">Date:</span> %s
                            </div>
                            <div class="detail-row">
                                <span class="label">Time:</span> %s
                            </div>
                            <div class="detail-row">
                                <span class="label">Passengers:</span> %d
                            </div>
                            <div class="detail-row">
                                <span class="label">Total Cost:</span> $%.2f
                            </div>
                            
                            <p>Please find your ticket attached to this email.</p>
                            
                            <div class="footer">
                                <p>Thank you for choosing our service!</p>
                                <p>Happy Journey!</p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                    ticket.getPassengerName(),
                    ticket.getStartingPoint(),
                    ticket.getDestinationPoint(),
                    formattedDate,
                    ticket.getTravelTime(),
                    ticket.getPassengerCount(),
                    ticket.getTripCost()
            );

            helper.setText(emailContent, true);

            FileSystemResource file = new FileSystemResource(new File(ticketPdfPath));
            helper.addAttachment("train_ticket.pdf", file);

            emailSender.send(message);
            logger.info("Confirmation email sent to: {} for passenger: {}",
                    to, ticket.getPassengerName());
        } catch (Exception e) {
            logger.error("Failed to send email to: {} for passenger: {}",
                    to, ticket.getPassengerName(), e);
            throw new RuntimeException("Failed to send confirmation email", e);
        }
    }
}
