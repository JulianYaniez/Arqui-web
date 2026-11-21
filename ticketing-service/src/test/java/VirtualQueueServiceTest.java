import com.arquiweb.ticketing.event.domain.TicketingEvent;
import com.arquiweb.ticketing.event.service.EventQueryService;
import com.arquiweb.ticketing.infrastructure.exception.ForbiddenException;
import com.arquiweb.ticketing.queue.domain.enums.QueueStatus;
import com.arquiweb.ticketing.queue.dto.response.QueueStatusResponse;
import com.arquiweb.ticketing.queue.service.VirtualQueueService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class VirtualQueueServiceTest {

    private static final int BATCH_SIZE = 10;
    private static final long ADMISSION_TTL_SECONDS = 600L;
    private static final String EVENT_ID = "64b1f2c3d4e5f6a7b8c9d0e1";
    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final String ACTIVE_EVENTS_KEY = "queue:active-events";
    private static final String WAITING_KEY = "queue:waiting:" + EVENT_ID;
    private static final String ADMITTED_KEY = "queue:admitted:" + EVENT_ID + ":" + USER_ID;
    private static final String SOLD_OUT_KEY = "queue:sold-out:" + EVENT_ID;

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private SetOperations<String, String> setOps;

    @Mock
    private ZSetOperations<String, String> zSetOps;

    @Mock
    private EventQueryService eventQueryService;

    private VirtualQueueService queueService;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        lenient().when(redis.opsForValue()).thenReturn(valueOps);
        lenient().when(redis.opsForSet()).thenReturn(setOps);
        lenient().when(redis.opsForZSet()).thenReturn(zSetOps);

        queueService = new VirtualQueueService(
                redis,
                BATCH_SIZE,
                ADMISSION_TTL_SECONDS,
                eventQueryService
        );
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testJoinQueue_AlreadySoldOutInRedis_ReturnsSoldOut() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(true);

        QueueStatusResponse response = queueService.joinQueue(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.SOLD_OUT);
        Assert.assertNull(response.position());
        Assert.assertNull(response.expiresInSeconds());
        verifyNoInteractions(eventQueryService);
    }

    @Test
    public void testJoinQueue_EventSalesDisabled_MarksSoldOutAndReturnsSoldOut() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);

        TicketingEvent event = new TicketingEvent(EVENT_ID, false, true);
        when(eventQueryService.getById(EVENT_ID)).thenReturn(event);

        QueueStatusResponse response = queueService.joinQueue(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.SOLD_OUT);
        verify(valueOps, times(1)).set(SOLD_OUT_KEY, "true", Duration.ofHours(24));
        verify(redis, times(1)).delete(WAITING_KEY);
        verify(setOps, times(1)).remove(ACTIVE_EVENTS_KEY, EVENT_ID);
    }

    @Test
    public void testJoinQueue_NotHighDemand_ReturnsAdmittedImmediately() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);

        TicketingEvent event = new TicketingEvent(EVENT_ID, true, false);
        when(eventQueryService.getById(EVENT_ID)).thenReturn(event);

        QueueStatusResponse response = queueService.joinQueue(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.ADMITTED);
        Assert.assertEquals(response.position(), Long.valueOf(0L));
        verifyNoInteractions(zSetOps);
    }

    @Test
    public void testJoinQueue_AlreadyAdmitted_ReturnsAdmittedWithTtl() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);

        TicketingEvent event = new TicketingEvent(EVENT_ID, true, true);
        when(eventQueryService.getById(EVENT_ID)).thenReturn(event);

        when(redis.hasKey(ADMITTED_KEY)).thenReturn(true);
        when(redis.getExpire(ADMITTED_KEY, TimeUnit.SECONDS)).thenReturn(450L);

        QueueStatusResponse response = queueService.joinQueue(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.ADMITTED);
        Assert.assertEquals(response.position(), Long.valueOf(0L));
        Assert.assertEquals(response.expiresInSeconds(), Long.valueOf(450L));
        verifyNoInteractions(zSetOps);
    }

    @Test
    public void testJoinQueue_EntersWaitingQueue_ReturnsWaitingWithPosition() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);

        TicketingEvent event = new TicketingEvent(EVENT_ID, true, true);
        when(eventQueryService.getById(EVENT_ID)).thenReturn(event);

        when(redis.hasKey(ADMITTED_KEY)).thenReturn(false);
        when(zSetOps.rank(WAITING_KEY, USER_ID)).thenReturn(4L);

        QueueStatusResponse response = queueService.joinQueue(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.WAITING);
        Assert.assertEquals(response.position(), Long.valueOf(5L)); // rank + 1
        verify(zSetOps, times(1)).addIfAbsent(eq(WAITING_KEY), eq(USER_ID.toString()), anyDouble());
        verify(setOps, times(1)).add(ACTIVE_EVENTS_KEY, EVENT_ID);
    }

    @Test
    public void testJoinQueue_RankReturnsNull_DefaultsPositionToOne() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);

        TicketingEvent event = new TicketingEvent(EVENT_ID, true, true);
        when(eventQueryService.getById(EVENT_ID)).thenReturn(event);

        when(redis.hasKey(ADMITTED_KEY)).thenReturn(false);
        when(zSetOps.rank(WAITING_KEY, USER_ID)).thenReturn(null);

        QueueStatusResponse response = queueService.joinQueue(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.WAITING);
        Assert.assertEquals(response.position(), Long.valueOf(1L));
    }

    @Test
    public void testGetStatus_SoldOut_ReturnsSoldOut() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(true);

        QueueStatusResponse response = queueService.getStatus(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.SOLD_OUT);
    }

    @Test
    public void testGetStatus_Admitted_ReturnsAdmittedWithTtl() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);
        when(redis.hasKey(ADMITTED_KEY)).thenReturn(true);
        when(redis.getExpire(ADMITTED_KEY, TimeUnit.SECONDS)).thenReturn(300L);

        QueueStatusResponse response = queueService.getStatus(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.ADMITTED);
        Assert.assertEquals(response.position(), Long.valueOf(0L));
        Assert.assertEquals(response.expiresInSeconds(), Long.valueOf(300L));
    }

    @Test
    public void testGetStatus_Waiting_ReturnsWaitingWithPosition() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);
        when(redis.hasKey(ADMITTED_KEY)).thenReturn(false);
        when(zSetOps.rank(WAITING_KEY, USER_ID)).thenReturn(2L);

        QueueStatusResponse response = queueService.getStatus(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.WAITING);
        Assert.assertEquals(response.position(), Long.valueOf(3L));
    }

    @Test
    public void testGetStatus_NotInQueue_ReturnsNotInQueue() {
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);
        when(redis.hasKey(ADMITTED_KEY)).thenReturn(false);
        when(zSetOps.rank(WAITING_KEY, USER_ID)).thenReturn(null);

        QueueStatusResponse response = queueService.getStatus(EVENT_ID, USER_ID);

        Assert.assertEquals(response.status(), QueueStatus.NOT_IN_QUEUE);
        Assert.assertNull(response.position());
    }

    @Test
    public void testAdmitNextBatch_NoActiveEvents_DoesNothing() {
        when(setOps.members(ACTIVE_EVENTS_KEY)).thenReturn(Collections.emptySet());

        queueService.admitNextBatch();

        verifyNoInteractions(zSetOps, valueOps);
    }

    @Test
    public void testAdmitNextBatch_EventSoldOut_CleansQueueAndRemovesFromActive() {
        when(setOps.members(ACTIVE_EVENTS_KEY)).thenReturn(Set.of(EVENT_ID));
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(true);

        queueService.admitNextBatch();

        verify(redis, times(1)).delete(WAITING_KEY);
        verify(setOps, times(1)).remove(ACTIVE_EVENTS_KEY, EVENT_ID);
        verifyNoInteractions(zSetOps);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAdmitNextBatch_AdmitsUsersAndRemovesEventWhenEmpty() {
        when(setOps.members(ACTIVE_EVENTS_KEY)).thenReturn(Set.of(EVENT_ID));
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);

        ZSetOperations.TypedTuple<String> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn(USER_ID.toString());
        when(zSetOps.popMin(WAITING_KEY, BATCH_SIZE)).thenReturn(Set.of(tuple));
        when(zSetOps.zCard(WAITING_KEY)).thenReturn(0L);

        queueService.admitNextBatch();

        verify(valueOps, times(1)).set(ADMITTED_KEY, "true", Duration.ofSeconds(ADMISSION_TTL_SECONDS));
        verify(setOps, times(1)).remove(ACTIVE_EVENTS_KEY, EVENT_ID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAdmitNextBatch_UsersStillWaiting_KeepsEventActive() {
        when(setOps.members(ACTIVE_EVENTS_KEY)).thenReturn(Set.of(EVENT_ID));
        when(redis.hasKey(SOLD_OUT_KEY)).thenReturn(false);

        ZSetOperations.TypedTuple<String> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn(USER_ID.toString());
        when(zSetOps.popMin(WAITING_KEY, BATCH_SIZE)).thenReturn(Set.of(tuple));
        when(zSetOps.zCard(WAITING_KEY)).thenReturn(5L);

        queueService.admitNextBatch();

        verify(valueOps, times(1)).set(ADMITTED_KEY, "true", Duration.ofSeconds(ADMISSION_TTL_SECONDS));
        verify(setOps, never()).remove(ACTIVE_EVENTS_KEY, EVENT_ID);
    }

    @Test
    public void testValidateAndConsumeAdmission_Admitted_DeletesAdmissionKey() {
        when(redis.hasKey(ADMITTED_KEY)).thenReturn(true);

        queueService.validateAndConsumeAdmission(EVENT_ID, USER_ID);

        verify(redis, times(1)).delete(ADMITTED_KEY);
    }

    @Test(expectedExceptions = ForbiddenException.class)
    public void testValidateAndConsumeAdmission_NotAdmitted_ThrowsForbiddenException() {
        when(redis.hasKey(ADMITTED_KEY)).thenReturn(false);

        queueService.validateAndConsumeAdmission(EVENT_ID, USER_ID);

        verify(redis, never()).delete(anyString());
    }
}