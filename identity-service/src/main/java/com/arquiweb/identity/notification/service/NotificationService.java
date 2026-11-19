package com.arquiweb.identity.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000L;

    private final JavaMailSender mailSender;


}
