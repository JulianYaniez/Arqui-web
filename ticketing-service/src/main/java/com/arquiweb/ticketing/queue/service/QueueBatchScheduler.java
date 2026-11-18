package com.arquiweb.ticketing.queue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueBatchScheduler {

    private final VirtualQueueService queueService;


    @Scheduled(fixedDelayString = "${app.config.queue.batch-interval-ms}")
    public void releaseQueueBatch() {
        queueService.admitNextBatch();
    }
}
