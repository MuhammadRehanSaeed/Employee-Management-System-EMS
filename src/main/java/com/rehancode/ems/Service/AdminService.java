package com.rehancode.ems.Service;

import com.rehancode.ems.Dto.*;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Model.UsersModel;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;


public interface AdminService {

    // Admin registers a user (employee or staff)
    ApiResponse<UserResponseDTO> registerUser(UserRequestDTO userRequestDTO);

    ApiResponse<EmpResponseDTO> createEmployee(EmpRequestDTO empRequestDTO);

    ApiResponse<UserResponseDTO> getUser(@Valid Long id);

    ApiResponse<String> deleteUser(@Valid Long id);

     ApiResponse<Page<UserResponseDTO>> getAllUsers(int page, int size);


    ApiResponse<Page<EmpResponseDTO>> getAllEmp(int page, int size);

    ApiResponse<EmpResponseDTO> getEmp(@Valid Long id);

    ApiResponse<String> deleteEmp(@Valid Long id);

    ApiResponse<String> updateEmp(@Valid Long id, EmpUpdateDTO empUpdateDTO);
}
