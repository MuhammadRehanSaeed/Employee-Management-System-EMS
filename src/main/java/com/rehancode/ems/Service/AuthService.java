package com.rehancode.ems.Service;

import com.rehancode.ems.Dto.LoginRequestDTO;
import com.rehancode.ems.Dto.LoginResponseDTO;
import com.rehancode.ems.Exception.ApiResponse;

public interface AuthService {
    ApiResponse<LoginResponseDTO> login(LoginRequestDTO loginRequestDTO);

    ApiResponse<String> logout(String token);
}
