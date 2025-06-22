# Train Ticket Booking System

A web-based train ticket booking application built with Spring Boot that allows users to book train tickets between various West Coast stations.

## Features
- User-friendly booking interface
- Dynamic ticket price calculation based on distance
- Automated PDF ticket generation with QR code
- Optional email confirmation
- Responsive design for desktop and mobile
- Real-time validation
- Downloadable tickets

## Technologies Used
- Java 17
- Spring Boot 3.1.5
- Thymeleaf template engine
- iText PDF 5.5.13.3 for PDF generation
- Spring Mail for email services
- HTML/CSS for frontend
- Maven for dependency management
- SLF4J/Logback for logging

## Key Features
1. **Booking System**
   - Station selection
   - Date and time scheduling
   - Passenger information
   - Multiple passenger booking
   - Price calculation

2. **PDF Ticket Generation**
   - Professional layout
   - QR code integration
   - Background styling
   - Passenger details
   - Journey information

3. **Email Integration**
   - Optional email confirmation
   - HTML formatted emails
   - PDF ticket attachment
   - Styled email template

4. **Error Handling**
   - User-friendly error messages
   - Form validation
   - Comprehensive logging
   - Error page template

## Setup and Installation

1. **Prerequisites**
   - Java 17 or higher
   - Maven
   - Git
2. Clone the Repository
   
3. Create application.properties in src/main/resources

4. Build and Run
mvn clean install
mvn spring-boot:run

5. Access Application 
http://localhost:8080


## Usage

Fill in the booking form:

- Enter passenger name
- Select starting and destination stations
- Choose travel date and time
- Specify number of passengers
- Optionally provide email for confirmation
- Review booking details on confirmation page

Download PDF ticket

Check email (if provided) for confirmation

**Logging**
- Application logs are stored in train-booking.log
- Includes booking confirmations, errors, and system events
- Different log levels for various operations
- PDF Tickets
- Generated tickets are stored in tickets/ directory
- Unique filename for each booking
- Professional layout with background image
- Includes QR code for validation

**Future Enhancements**
- [ ] User authentication and accounts
- [ ] Seat selection functionality
- [ ] Payment gateway integration
- [ ] Mobile app development
- [ ] Real-time ticket availability
- [ ] Multiple language support
