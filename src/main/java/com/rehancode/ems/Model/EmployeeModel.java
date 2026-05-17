package com.rehancode.ems.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rehancode.ems.Enum.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "employees")
@Data
public class EmployeeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeCode;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false)
    private double salary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false,unique = true)
    private UsersModel user;

    @OneToMany(mappedBy = "employee",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<AttendanceModel> attendance;

    @CreationTimestamp
    private Timestamp createdAt;


    @UpdateTimestamp
    private Timestamp updatedAt;
}
