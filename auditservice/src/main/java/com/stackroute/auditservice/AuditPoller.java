package com.stackroute.auditservice;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditPoller {

    private final AuditEventRepository repo;

    public AuditPoller(AuditEventRepository repo) {
        this.repo = repo;
    }

    @Scheduled(fixedDelay = 5000)
    public void poll() {
      //  List<AuditEvent> events = repo.findByProcessedFalse();
    	
    	List<AuditEvent> events=repo.findAll();
        for (AuditEvent e : events) {
            System.out.println("AUDIT PROCESSING => " + e.getActionName()
                    + " | " + e.getUsername()
                    + " | " + e.getEndpoint()
                    + " | " + e.getStatus());

            e.setProcessed(true);
            repo.save(e);
        }
    }
}