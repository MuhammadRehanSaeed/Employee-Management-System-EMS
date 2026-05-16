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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("getUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser(@Valid @PathVariable Long id){
        ApiResponse<UserResponseDTO> response= adminService.getUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@Valid @PathVariable Long id){
        ApiResponse<String> response= adminService.deleteUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
