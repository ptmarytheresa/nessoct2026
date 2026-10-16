package com.stackroute.summaryapp.service;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.stackroute.summaryapp.dto.AuditEvent;

@Service
public class AuditPublisher {

    private final ApplicationEventPublisher publisher;

    public AuditPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }


    public void publish(String action, String user, String endpoint, String status) {
       publisher.publishEvent(new AuditEvent(action, user, endpoint, status, LocalDateTime.now()));
    }
}