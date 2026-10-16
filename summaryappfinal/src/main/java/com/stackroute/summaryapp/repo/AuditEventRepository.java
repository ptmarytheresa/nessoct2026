package com.stackroute.summaryapp.repo;


import org.springframework.data.jpa.repository.JpaRepository;

import com.stackroute.summaryapp.dto.AuditEvent;

import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findByProcessedFalse();
}