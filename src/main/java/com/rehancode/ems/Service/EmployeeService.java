package com.rehancode.ems.Service;

import com.rehancode.ems.Dto.ChangePasswordDTO;
import com.rehancode.ems.Dto.EmpResponseDTO;
import com.rehancode.ems.Dto.EmpUpdateDTO;
import com.rehancode.ems.Exception.ApiResponse;
import jakarta.validation.Valid;

public interface EmployeeService {

    ApiResponse<EmpResponseDTO> getMyProfile();

    ApiResponse<String> updateProfile(EmpUpdateDTO empUpdateDTO);

    ApiResponse<String> changePassword(@Valid ChangePasswordDTO dto);
}
