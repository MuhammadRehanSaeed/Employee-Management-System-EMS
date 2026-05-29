package com.rehancode.ems.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveResponseDTO {
    private Long id;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private long days;
}
