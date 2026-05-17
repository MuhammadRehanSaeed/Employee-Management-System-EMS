package com.rehancode.ems.Service;

import com.rehancode.ems.Dto.AttendanceHistoryDTO;
import com.rehancode.ems.Exception.ApiResponse;

import java.util.List;

public interface AttendanceService {
    ApiResponse<String> checkIn();

    ApiResponse<String> checkOut();

    ApiResponse<AttendanceHistoryDTO> getTodayAttendance();

    ApiResponse<List<AttendanceHistoryDTO>> getAttendanceHistory();
}
