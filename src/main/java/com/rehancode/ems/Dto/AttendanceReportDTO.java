package com.rehancode.ems.Dto;

import com.rehancode.ems.Enum.AttendanceStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceReportDTO {
    private Long employeeId;
    private String employeeName;
    private String designation;

    private LocalDate attendanceDate;

    private AttendanceStatus status;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private Long totalWorkingMinutes;

    private Boolean checkedIn;
    private Boolean checkedOut;
}
