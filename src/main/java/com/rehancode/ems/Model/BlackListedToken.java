package com.rehancode.ems.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "BlackListed_Tokens")
@Data
public class BlackListedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "JTI")
    private String jti;

    @Column(name = "EXPIRY")
    private LocalDateTime expiry;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

}
