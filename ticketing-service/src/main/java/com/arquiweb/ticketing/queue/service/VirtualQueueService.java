package com.arquiweb.ticketing.queue.service;

import com.arquiweb.ticketing.event.domain.TicketingEvent;
import com.arquiweb.ticketing.event.service.EventQueryService;
import com.arquiweb.ticketing.infrastructure.exception.ForbiddenException;
import com.arquiweb.ticketing.queue.domain.enums.QueueStatus;
import com.arquiweb.ticketing.queue.dto.response.QueueStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class VirtualQueueService {

    private static final String ACTIVE_EVENTS_KEY = "queue:active-events";

    private final StringRedisTemplate redis;
    private final int batchSize;
    private final long admissionTtlSeconds;
    private final EventQueryService eventQueryService;

    public VirtualQueueService(
            StringRedisTemplate redis,
            @Value("${app.config.queue.batch-size}") int batchSize,
            @Value("${app.config.queue.admission-ttl-seconds}") long admissionTtlSeconds,
            EventQueryService eventQueryService) {
        this.redis = redis;
        this.batchSize = batchSize;
        this.admissionTtlSeconds = admissionTtlSeconds;
        this.eventQueryService = eventQueryService;
    }

    private String waitingKey(String eventId) {
        return "queue:waiting:" + eventId;
    }

    private String admittedKey(String eventId, UUID userId) {
        return "queue:admitted:" + eventId + ":" + userId;
    }

    private String soldOutKey(String eventId) {
        return "queue:sold-out:" + eventId;
    }

    private void markSoldOut(String eventId) {
        redis.opsForValue().set(soldOutKey(eventId), "true", Duration.ofHours((24)));
        redis.delete((waitingKey(eventId)));
        redis.opsForSet().remove(ACTIVE_EVENTS_KEY, eventId);
    }

    public QueueStatusResponse joinQueue(String eventId, UUID userId) {
        if (Boolean.TRUE.equals(redis.hasKey(soldOutKey(eventId)))) {
            return new QueueStatusResponse(QueueStatus.SOLD_OUT, null, null);
        }

        TicketingEvent event = eventQueryService.getById(eventId);

        if (!event.isSalesEnabled()) {
            markSoldOut(eventId);
            return new QueueStatusResponse(QueueStatus.SOLD_OUT, null, null);
        }

        if (!event.isHighDemand()) {
            return new QueueStatusResponse(QueueStatus.ADMITTED, 0L, null);
        }

        String admitKey = admittedKey(eventId, userId);
        if (Boolean.TRUE.equals(redis.hasKey(admitKey))) {
            Long ttl = redis.getExpire(admitKey, TimeUnit.SECONDS);
            return new QueueStatusResponse(QueueStatus.ADMITTED, 0L, ttl);
        }

        String waitKey = waitingKey(eventId);
        redis.opsForZSet().addIfAbsent(waitKey, userId.toString(), System.currentTimeMillis());

        redis.opsForSet().add(ACTIVE_EVENTS_KEY, eventId);

        Long rank = redis.opsForZSet().rank(waitKey, userId.toString());
        Long position = (rank != null) ? rank + 1 : 1L;

        return new QueueStatusResponse(QueueStatus.WAITING, position, null);
    }

    public QueueStatusResponse getStatus(String eventId, UUID userId) {
        if (Boolean.TRUE.equals(redis.hasKey(soldOutKey(eventId)))) {
            return new QueueStatusResponse(QueueStatus.SOLD_OUT, null, null);
        }

        String admitKey = admittedKey(eventId, userId);
        if (Boolean.TRUE.equals(redis.hasKey(admitKey))) {
            Long ttl = redis.getExpire(admitKey, TimeUnit.SECONDS);
            return new QueueStatusResponse(QueueStatus.ADMITTED, 0L, ttl);
        }

        Long rank = redis.opsForZSet().rank(waitingKey(eventId), userId.toString());
        if (rank != null) {
            return new QueueStatusResponse(QueueStatus.WAITING, rank + 1, null);
        }

        return new QueueStatusResponse(QueueStatus.NOT_IN_QUEUE, null, null);
    }


    public void admitNextBatch() {
        Set<String> activeEvents = redis.opsForSet().members(ACTIVE_EVENTS_KEY);
        if (activeEvents == null || activeEvents.isEmpty()) {
            return;
        }

        for (String eventId : activeEvents) {

            if (Boolean.TRUE.equals(redis.hasKey(soldOutKey(eventId)))) {
                redis.delete(waitingKey(eventId));
                redis.opsForSet().remove(ACTIVE_EVENTS_KEY, eventId);
                continue;
            }

            String waitKey = waitingKey(eventId);
            Set<ZSetOperations.TypedTuple<String>> batch = redis.opsForZSet().popMin(waitKey, batchSize);

            if (batch != null) {
                for (ZSetOperations.TypedTuple<String> entry : batch) {
                    String userId = entry.getValue();
                    if (userId != null) {
                        String admitKey = admittedKey(eventId, UUID.fromString(userId));
                        redis.opsForValue().set(admitKey, "true", Duration.ofSeconds(admissionTtlSeconds));
                    }
                }
            }

            Long remaining = redis.opsForZSet().zCard(waitKey);
            if (remaining == null || remaining == 0) {
                redis.opsForSet().remove(ACTIVE_EVENTS_KEY, eventId);
            }
        }
    }

    public void validateAndConsumeAdmission(String eventId, UUID userId) {
        String admitKey = admittedKey(eventId, userId);
        if (!Boolean.TRUE.equals(redis.hasKey(admitKey))) {
            throw new ForbiddenException("Must wait on queue first.");
        }
        redis.delete(admitKey);
    }
}
