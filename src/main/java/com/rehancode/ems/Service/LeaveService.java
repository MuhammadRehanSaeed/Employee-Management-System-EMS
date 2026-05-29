package com.rehancode.ems.Service;

import com.rehancode.ems.Dto.LeaveRequestDTO;
import com.rehancode.ems.Dto.LeaveResponseDTO;
import com.rehancode.ems.Exception.ApiResponse;

import java.util.List;

public interface LeaveService {
    ApiResponse<String> applyLeave(LeaveRequestDTO request);

    ApiResponse<List<LeaveResponseDTO>> getLeaves();

    ApiResponse<List<LeaveResponseDTO>> getLeave();

    ApiResponse<List<LeaveResponseDTO>> getLeaveById(Long id);

    ApiResponse<String> approveLeave(Long id);

    ApiResponse<String> rejectLeave(Long id);
}
