package com.rehancode.ems.Controller;

import com.rehancode.ems.Dto.EmpRequestDTO;
import com.rehancode.ems.Dto.EmpResponseDTO;
import com.rehancode.ems.Dto.UserRequestDTO;
import com.rehancode.ems.Dto.UserResponseDTO;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private AdminService adminService;
    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerEmp(@Valid @RequestBody UserRequestDTO userRequestDTO){
        ApiResponse<UserResponseDTO> response= adminService.registerUser(userRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("createEmp")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmpResponseDTO>> registerEmp(@Valid @RequestBody EmpRequestDTO empRequestDTO){
        ApiResponse<EmpResponseDTO> response= adminService.createEmployee(empRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
