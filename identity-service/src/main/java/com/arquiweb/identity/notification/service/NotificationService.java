package com.arquiweb.identity.notification.service;

import com.arquiweb.identity.auth.domain.User;
import com.arquiweb.identity.auth.service.AuthService;
import com.arquiweb.identity.infrastructure.messaging.events.EventChangedNotification;
import com.arquiweb.identity.infrastructure.messaging.events.FavoriteArtistNotification;
import com.arquiweb.identity.infrastructure.messaging.events.PurchaseConfirmedNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final AuthService authService;

    public void trySendEmail(String to, String subject, String body){
        try {
            send(to, subject, body);
        } catch (MailException ex) {
            log.error("Fallback: Couldn't send email to {}", to);
        }
    }

    @Retryable(includes = MailException.class, maxRetries = 2, delay = 1000)
    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@ticketya.local");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("Email sent to {}, subject: {}", to, subject);
    }

    public void notifyPurchaseConfirmation(PurchaseConfirmedNotification event) {
        User user = authService.getById(event.userId());

        String formattedQrs = event.qrCodes().stream()
        .map(qr -> "  - " + qr)
        .collect(Collectors.joining("\n"));

        String subject = "TicketYa - Purchase Confirmation";

        String body = """
            Your purchase on TicketYa has been confirmed!

            Payment ID: %s

            Your access QR codes (present them at the venue entrance):
            %s

            Enjoy the event!
            TicketYa Team
        """.formatted(event.paymentId(), formattedQrs);

        trySendEmail(user.getEmail(), subject, body);
    }

    public void notifyEventChanged(EventChangedNotification event) {
        List<User> users = authService.getAllById(event.usersId());

        boolean isCancelled = "CANCELLED".equalsIgnoreCase(event.changeType());

        String subject = isCancelled
                ? "TicketYa - Event Cancelled (ID: " + event.eventId() + ")"
                : "TicketYa - Event Updated (ID: " + event.eventId() + ")";

        String statusLabel = isCancelled ? "CANCELLED" : "MODIFIED";
        String actionNote = isCancelled
                ? "All your purchased tickets for this event have been automatically queued for a full refund."
                : "Please check the event page on TicketYa for the latest details. Your existing tickets remain valid.";

        String body = """
                Hello, we are writing to inform you about an important update regarding an event you hold tickets for.
        
                Event ID: %s
                Status: %s
        
                %s
        
                TicketYa Team
                """.formatted(event.eventId(), statusLabel, actionNote);

        for (User user : users) {
            trySendEmail(user.getEmail(), subject, body);
        }
    }

    public void notifyFavoriteArtistPublish(FavoriteArtistNotification event) {
        List<User> users = authService.getAllById(event.usersId());

        String subject = "New event in " + event.city() + ": " + event.eventName();

        String artistsList = String.join(", ", event.artists());

        String body = """
                Good news! One of your favorite artists just published a new event in your city.
        
                Event: %s (ID: %s)
                Artists: %s
                City: %s
        
                Log in to TicketYa to check available sectors and grab your tickets.
        
                TicketYa Team
                """.formatted(event.eventName(), event.eventId(), artistsList, event.city());

        for (User user : users) {
            trySendEmail(user.getEmail(), subject, body);
        }
    }
}
