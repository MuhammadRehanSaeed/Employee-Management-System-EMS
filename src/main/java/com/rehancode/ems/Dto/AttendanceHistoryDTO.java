package com.rehancode.ems.Dto;

import com.rehancode.ems.Enum.AttendanceStatus;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceHistoryDTO {
    private Long id;

    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Long totalWorkingMinutes;

    private Long employeeId;
    private AttendanceStatus status;
}
