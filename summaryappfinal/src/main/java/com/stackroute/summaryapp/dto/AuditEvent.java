package com.stackroute.summaryapp.dto;

import java.time.LocalDateTime;

 
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actionName;
    private String username;
    private String endpoint;
    private String status;
    private boolean processed = false;
    private LocalDateTime createdAt = LocalDateTime.now();
public AuditEvent() {};
    public AuditEvent(String action, String user, String endpoint2, String status2, LocalDateTime localDateTime) {}
    public Long getId() { return id; }
    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}