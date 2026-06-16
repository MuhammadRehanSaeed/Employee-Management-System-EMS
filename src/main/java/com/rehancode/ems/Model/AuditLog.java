package com.rehancode.ems.Model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who performed the action
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String performedBy;

    // What happened
    @Column(name = "action")
    private String action;

    // Example: USER_REGISTER, USER_UPDATE, USER_DELETE
    @Column(name = "entity_name")
    private String entityName;


    // Additional information
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "ip_address")
    private String ipAddress;


    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}