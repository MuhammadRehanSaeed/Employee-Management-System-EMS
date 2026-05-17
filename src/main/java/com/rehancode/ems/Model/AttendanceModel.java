package com.rehancode.ems.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rehancode.ems.Enum.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Data
public class AttendanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate attendanceDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime checkInTime;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime checkOutTime;

    private Long totalWorkingMinutes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private EmployeeModel employee;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}