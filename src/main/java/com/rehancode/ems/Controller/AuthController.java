package com.rehancode.ems.Controller;

import com.rehancode.ems.Dto.LoginRequestDTO;
import com.rehancode.ems.Dto.LoginResponseDTO;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        log.info("POST /api/auth/login username='{}'", loginRequestDTO.getUsername());
        ApiResponse<LoginResponseDTO> response = authService.login(loginRequestDTO);
        log.info("POST /api/auth/login completed username='{}' role='{}'",
                loginRequestDTO.getUsername(), response.getData().getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
