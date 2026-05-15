package com.rehancode.ems.Dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.rehancode.ems.Enum.Status;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class EmpResponseDTO {
    private Long id;

    private String employeeCode;

    private String fullName;

    private String phoneNumber;

    private String department;

    private String designation;

    private double salary;

    private Status status;

    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;

    private Long userId;

    private String username; // optional (useful for frontend display)

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
