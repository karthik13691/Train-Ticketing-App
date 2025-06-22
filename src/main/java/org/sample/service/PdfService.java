package org.sample.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.sample.model.Ticket;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {
    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    public String generateTicketPdf(Ticket ticket) {
        File ticketDir = new File("tickets");
        if (!ticketDir.exists()) {
            ticketDir.mkdir();
        }

        String fileName = "ticket_" + System.currentTimeMillis() + ".pdf";
        String filePath = "tickets/" + fileName;

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add background image
            try {
                Image background = Image.getInstance(new URL("https://ttc.com/wp-content/uploads/2020/11/rocky_mountaineer-1.jpg"));
                background.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                background.setAbsolutePosition(0, 0);
                document.add(background);
            } catch (Exception e) {
                logger.warn("Could not add background image", e);
            }

            // Add semi-transparent overlay
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.saveState();
            PdfGState state = new PdfGState();
            state.setFillOpacity(0.7f);
            canvas.setGState(state);
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(50, 50, PageSize.A4.getWidth() - 100, PageSize.A4.getHeight() - 100);
            canvas.fill();
            canvas.restoreState();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Train Ticket", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(50);
            document.add(title);

            // Decorative line
            LineSeparator line = new LineSeparator();
            line.setLineColor(BaseColor.DARK_GRAY);
            document.add(new Chunk(line));

            // Format date
            String formattedDate = ticket.getTravelDate().format(
                    DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

            // Content table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(80);
            table.setSpacingBefore(30);
            table.setSpacingAfter(30);

            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            // Add passenger name first
            addTableRow(table, "Passenger Name:", ticket.getPassengerName(), labelFont, valueFont);
            addTableRow(table, "From:", ticket.getStartingPoint(), labelFont, valueFont);
            addTableRow(table, "To:", ticket.getDestinationPoint(), labelFont, valueFont);
            addTableRow(table, "Date:", formattedDate, labelFont, valueFont);
            addTableRow(table, "Time:", ticket.getTravelTime(), labelFont, valueFont);
            addTableRow(table, "Passengers:", String.valueOf(ticket.getPassengerCount()), labelFont, valueFont);
            addTableRow(table, "Total Cost:", "$" + String.format("%.2f", ticket.getTripCost()), labelFont, valueFont);

            document.add(table);

            // Footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Thank you for choosing our service!", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            // QR Code or Barcode (optional)
            try {
                BarcodeQRCode qrCode = new BarcodeQRCode(
                        String.format("Ticket:%s,Date:%s,Time:%s",
                                ticket.getPassengerName(),
                                formattedDate,
                                ticket.getTravelTime()),
                        100, 100, null);
                Image qrCodeImage = qrCode.getImage();
                qrCodeImage.setAlignment(Element.ALIGN_CENTER);
                document.add(qrCodeImage);
            } catch (Exception e) {
                logger.warn("Could not generate QR code", e);
            }

            document.close();
            logger.info("PDF ticket generated: {}", fileName);
            return filePath;

        } catch (Exception e) {
            logger.error("Failed to generate PDF", e);
            throw new RuntimeException("Failed to generate ticket PDF", e);
        }
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));

        labelCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBorder(Rectangle.NO_BORDER);

        labelCell.setPadding(5);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}
