import com.arquiweb.identity.auth.domain.User;
import com.arquiweb.identity.auth.service.AuthService;
import com.arquiweb.identity.infrastructure.messaging.events.EventChangedNotification;
import com.arquiweb.identity.infrastructure.messaging.events.FavoriteArtistNotification;
import com.arquiweb.identity.infrastructure.messaging.events.PurchaseConfirmedNotification;
import com.arquiweb.identity.notification.service.NotificationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private AuthService authService;

    private NotificationService notificationService;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(mailSender, authService);
        notificationService = new NotificationService(mailSender, authService);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testSend_Successful_SendsSimpleMailMessage() {
        notificationService.send("user@test.com", "Asunto", "Cuerpo del mensaje");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        Assert.assertEquals(msg.getFrom(), "noreply@ticketya.local");
        Assert.assertEquals(msg.getTo(), new String[]{"user@test.com"});
        Assert.assertEquals(msg.getSubject(), "Asunto");
        Assert.assertEquals(msg.getText(), "Cuerpo del mensaje");
    }

    @Test
    public void testTrySendEmail_MailException_CatchesAndDoesNotPropagate() {
        doThrow(new MailSendException("SMTP down")).when(mailSender).send(any(SimpleMailMessage.class));

        notificationService.trySendEmail("user@test.com", "Asunto", "Cuerpo");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testNotifyPurchaseConfirmation_Successful_FormatsQrsAndSendsEmail() {
        UUID userId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        PurchaseConfirmedNotification event = mock(PurchaseConfirmedNotification.class);
        when(event.userId()).thenReturn(userId);
        when(event.paymentId()).thenReturn(paymentId);
        when(event.qrCodes()).thenReturn(List.of("QR-001", "QR-002"));

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("comprador@test.com");
        when(authService.getById(userId)).thenReturn(user);

        notificationService.notifyPurchaseConfirmation(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        Assert.assertEquals(msg.getTo(), new String[]{"comprador@test.com"});
        Assert.assertEquals(msg.getSubject(), "TicketYa - Purchase Confirmation");
        Assert.assertTrue(msg.getText().contains(paymentId.toString()));
        Assert.assertTrue(msg.getText().contains("  - QR-001"));
        Assert.assertTrue(msg.getText().contains("  - QR-002"));
    }

    @Test
    public void testNotifyEventChanged_Cancelled_SendsCancellationEmailToAllUsers() {
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        List<UUID> userIds = List.of(u1, u2);

        EventChangedNotification event = mock(EventChangedNotification.class);
        when(event.userIds()).thenReturn(userIds);
        when(event.eventId()).thenReturn("evt-999");
        when(event.changeType()).thenReturn("CANCELLED");

        User user1 = mock(User.class);
        when(user1.getEmail()).thenReturn("u1@test.com");
        User user2 = mock(User.class);
        when(user2.getEmail()).thenReturn("u2@test.com");
        when(authService.getAllById(userIds)).thenReturn(List.of(user1, user2));

        notificationService.notifyEventChanged(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(2)).send(captor.capture());

        List<SimpleMailMessage> sentMessages = captor.getAllValues();
        Assert.assertEquals(sentMessages.get(0).getSubject(), "TicketYa - Event Cancelled (ID: evt-999)");
        Assert.assertTrue(sentMessages.get(0).getText().contains("Status: CANCELLED"));
        Assert.assertTrue(sentMessages.get(0).getText().contains("queued for a full refund"));
        Assert.assertEquals(sentMessages.get(1).getTo(), new String[]{"u2@test.com"});
    }

    @Test
    public void testNotifyEventChanged_Modified_SendsUpdateEmail() {
        UUID u1 = UUID.randomUUID();
        List<UUID> userIds = List.of(u1);

        EventChangedNotification event = mock(EventChangedNotification.class);
        when(event.userIds()).thenReturn(userIds);
        when(event.eventId()).thenReturn("evt-555");
        when(event.changeType()).thenReturn("UPDATED"); // Distinto de CANCELLED

        User user1 = mock(User.class);
        when(user1.getEmail()).thenReturn("u1@test.com");
        when(authService.getAllById(userIds)).thenReturn(List.of(user1));

        notificationService.notifyEventChanged(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        Assert.assertEquals(msg.getSubject(), "TicketYa - Event Updated (ID: evt-555)");
        Assert.assertTrue(msg.getText().contains("Status: MODIFIED"));
        Assert.assertTrue(msg.getText().contains("existing tickets remain valid"));
    }

    @Test
    public void testNotifyFavoriteArtistPublish_Successful_SendsEmailToAllFollowers() {
        UUID u1 = UUID.randomUUID();
        List<UUID> userIds = List.of(u1);

        FavoriteArtistNotification event = mock(FavoriteArtistNotification.class);
        when(event.usersId()).thenReturn(userIds);
        when(event.city()).thenReturn("Tandil");
        when(event.eventName()).thenReturn("Rock en las Sierras");
        when(event.eventId()).thenReturn("evt-777");
        when(event.artists()).thenReturn(List.of("Divididos", "La Renga"));

        User user1 = mock(User.class);
        when(user1.getEmail()).thenReturn("fan@test.com");
        when(authService.getAllById(userIds)).thenReturn(List.of(user1));

        notificationService.notifyFavoriteArtistPublish(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        Assert.assertEquals(msg.getTo(), new String[]{"fan@test.com"});
        Assert.assertEquals(msg.getSubject(), "New event in Tandil: Rock en las Sierras");
        Assert.assertTrue(msg.getText().contains("Artists: Divididos, La Renga"));
        Assert.assertTrue(msg.getText().contains("City: Tandil"));
    }
}
