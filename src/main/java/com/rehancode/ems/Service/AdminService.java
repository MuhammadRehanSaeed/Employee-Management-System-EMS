package com.rehancode.ems.Service;

import com.rehancode.ems.Dto.EmpRequestDTO;
import com.rehancode.ems.Dto.EmpResponseDTO;
import com.rehancode.ems.Dto.UserRequestDTO;
import com.rehancode.ems.Dto.UserResponseDTO;
import com.rehancode.ems.Exception.ApiResponse;
import jakarta.validation.Valid;


public interface AdminService {

    // Admin registers a user (employee or staff)
    ApiResponse<UserResponseDTO> registerUser(UserRequestDTO userRequestDTO);

    ApiResponse<EmpResponseDTO> createEmployee(EmpRequestDTO empRequestDTO);
}
