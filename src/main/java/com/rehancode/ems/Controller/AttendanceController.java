package com.rehancode.ems.Controller;

import com.rehancode.ems.Dto.AttendanceHistoryDTO;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/employee")
public class AttendanceController {
    private AttendanceService attendanceService;
    public AttendanceController(AttendanceService attendanceService){
        this.attendanceService=attendanceService;
    }

    @PostMapping("checkIn")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<String>> checkIn(){
        ApiResponse<String> response=attendanceService.checkIn();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("checkOut")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<String>> checkOut(){
        ApiResponse<String> response=attendanceService.checkOut();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("today-attendance")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<AttendanceHistoryDTO>> getTodayAttendance(){
        ApiResponse<AttendanceHistoryDTO> response=attendanceService.getTodayAttendance();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("attendance-history")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<AttendanceHistoryDTO>>> getAttendanceHistory(){
        ApiResponse<List<AttendanceHistoryDTO>> response=attendanceService.getAttendanceHistory();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
