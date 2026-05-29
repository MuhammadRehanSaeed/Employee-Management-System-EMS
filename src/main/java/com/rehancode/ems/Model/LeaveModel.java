package com.rehancode.ems.Model;


import com.rehancode.ems.Enum.LeaveStatus;
import com.rehancode.ems.Enum.LeaveType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "Leaves")
public class LeaveModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Enumerated(EnumType.STRING)
    private LeaveStatus leaveStatus;

    private LocalDate startDate;
    private LocalDate endDate;
    private long days;
    private String reason;
    @ManyToOne
    @JoinColumn(name = "emp_id")
    private EmployeeModel employee;


    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
