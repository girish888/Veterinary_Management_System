# Email Reminder System for Veterinary Management Platform

## Overview

This system provides automatic email reminders for veterinary appointments, sending notifications to both pet owners and veterinarians on the day of scheduled appointments. The system is designed to be reliable, configurable, and easy to manage.

## Features

- **Automatic Daily Reminders**: Sends reminders at 8:00 AM daily for all appointments scheduled for that day
- **Hourly Checks**: Additional hourly checks for same-day appointments to ensure timely notifications
- **Dual Recipients**: Sends reminders to both pet owners and veterinarians
- **Retry Mechanism**: Implements exponential backoff retry logic for failed email sends
- **Manual Trigger**: Admin interface to manually trigger reminders when needed
- **Comprehensive Logging**: Detailed logging for monitoring and debugging
- **Configurable**: Easy to configure timing, email settings, and clinic information

## System Architecture

### Components

1. **EmailService** (`EmailService.java` & `EmailServiceImpl.java`)
   - Handles email composition and sending
   - Implements retry logic with exponential backoff
   - Generates personalized email content for both recipients

2. **AppointmentReminderService** (`AppointmentReminderService.java` & `AppointmentReminderServiceImpl.java`)
   - Business logic for processing reminders
   - Fetches appointments from database
   - Orchestrates email sending process

3. **AppointmentReminderScheduler** (`AppointmentReminderScheduler.java`)
   - Scheduled jobs using Spring's @Scheduled annotation
   - Daily reminders at 8:00 AM
   - Hourly checks for same-day appointments

4. **AppointmentReminderController** (`AppointmentReminderController.java`)
   - REST API endpoints for manual operations
   - Admin-only access for security
   - Manual trigger and status monitoring

5. **AppointmentReminderData** (`AppointmentReminderData.java`)
   - DTO containing all necessary data for reminders
   - Enriched with user and pet information

## Configuration

### Email Settings

Update `application.properties` with your email provider settings:

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

**Note**: For Gmail, you'll need to:
1. Enable 2-factor authentication
2. Generate an App Password
3. Use the App Password instead of your regular password

### Reminder Timing

```properties
# Appointment Reminder Configuration
app.reminder.enabled=true
app.reminder.time=08:00
app.reminder.cron=0 0 8 * * ?        # Daily at 8:00 AM
app.reminder.hourly.cron=0 0 * * * ?  # Every hour
```

### Clinic Information

```properties
# Clinic Information (used in email templates)
app.clinic.name=VetPet Clinic
app.clinic.address=123 Veterinary Street, City, State 12345
app.clinic.phone=+1-555-123-4567
```

## Email Templates

### Pet Owner Reminder

The system generates personalized emails for pet owners including:
- Greeting with owner's name
- Appointment date and time
- Pet name and species
- Veterinarian's name
- Reason for visit
- Clinic location and contact information
- Arrival instructions

### Veterinarian Reminder

Professional reminders for veterinarians including:
- Appointment details
- Pet and owner information
- Reason for visit
- Professional preparation reminders

## Database Requirements

### Required Repository Methods

The system requires the following method in `AppointmentRepository`:

```java
List<Appointment> findByDateTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);
```

### Data Enrichment

The system automatically enriches appointment data with:
- Owner information (name, email)
- Veterinarian information (name, email)
- Pet information (name, species)

## API Endpoints

### Admin-Only Endpoints

All endpoints require ADMIN role authentication:

- `POST /api/reminders/trigger/today` - Trigger today's reminders
- `POST /api/reminders/trigger/date/{date}` - Trigger reminders for specific date
- `GET /api/reminders/appointments/{date}` - Get appointments for specific date
- `GET /api/reminders/appointments/today` - Get today's appointments
- `POST /api/reminders/send/{appointmentId}` - Send reminder for specific appointment
- `GET /api/reminders/status` - Get system status

## Admin Interface

Access the reminder management interface at `/admin/reminders` (admin role required).

### Features:
- System status monitoring
- Manual reminder triggering
- Today's appointments view
- Individual appointment reminder sending
- Real-time status updates

## Security

- All reminder endpoints require ADMIN role
- CSRF protection enabled
- Secure email configuration
- Input validation and sanitization

## Monitoring and Logging

### Log Levels

- **INFO**: Normal operations, successful email sends
- **WARN**: Missing email addresses, configuration issues
- **ERROR**: Email sending failures, system errors
- **DEBUG**: Detailed operation information

### Key Metrics

- Email send success/failure rates
- Reminder processing times
- Appointment counts by date
- System uptime and health

## Troubleshooting

### Common Issues

1. **Email Not Sending**
   - Check SMTP configuration
   - Verify email credentials
   - Check firewall/network settings
   - Review application logs

2. **Reminders Not Triggering**
   - Verify scheduling is enabled
   - Check cron expressions
   - Ensure @EnableScheduling is present
   - Review Quartz configuration

3. **Missing Data**
   - Verify appointment data exists
   - Check user email addresses
   - Ensure proper relationships between entities

### Debug Mode

Enable debug logging for detailed information:

```properties
logging.level.com.vet.scheduler=DEBUG
logging.level.com.vet.service=DEBUG
logging.level.org.springframework.mail=DEBUG
```

## Performance Considerations

- Emails are sent asynchronously using CompletableFuture
- Database queries are optimized with proper indexing
- Scheduled jobs use efficient cron expressions
- Memory-based Quartz configuration for simplicity

## Future Enhancements

Potential improvements for production use:

1. **Database Tracking**: Store reminder history to prevent duplicates
2. **Email Templates**: Use Thymeleaf templates for HTML emails
3. **Multiple Email Providers**: Support for different SMTP services
4. **Advanced Scheduling**: Custom reminder timing per appointment
5. **Notification Preferences**: User-configurable reminder settings
6. **SMS Integration**: Text message reminders as alternative
7. **Analytics Dashboard**: Detailed reporting and metrics

## Testing

### Manual Testing

1. Create test appointments for today
2. Use admin interface to manually trigger reminders
3. Verify emails are received by both parties
4. Check logs for successful operations

### Integration Testing

1. Test with real email addresses
2. Verify retry mechanism with invalid emails
3. Test scheduling with different time zones
4. Validate error handling and logging

## Deployment

### Requirements

- Java 17+
- Spring Boot 3.2.3+
- MySQL 8.0+
- SMTP access for email sending

### Environment Variables

Consider using environment variables for sensitive configuration:

```bash
export SPRING_MAIL_USERNAME=your-email@gmail.com
export SPRING_MAIL_PASSWORD=your-app-password
export APP_CLINIC_NAME="Your Clinic Name"
```

## Support

For issues or questions:
1. Check application logs for error details
2. Verify configuration settings
3. Test email connectivity manually
4. Review database data integrity

---

**Note**: This system is designed for development and testing. For production use, consider implementing additional security measures, monitoring, and backup procedures.
