package com.rehancode.ems.Controller;

import com.rehancode.ems.Dto.*;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Service.AdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getUsers(@RequestParam ("page") int page, @RequestParam ("size") int size){
        ApiResponse<Page<UserResponseDTO>> response= adminService.getAllUsers(page,size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<EmpResponseDTO>>> getAllEmp(@RequestParam ("page") int page, @RequestParam ("size") int size){
        ApiResponse<Page<EmpResponseDTO>> response= adminService.getAllEmp(page,size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("getEmp/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmpResponseDTO>> getEmp(@Valid @PathVariable Long id){
        ApiResponse<EmpResponseDTO> response= adminService.getEmp(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("deleteEmp/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteEmp(@Valid @PathVariable Long id){
        ApiResponse<String> response= adminService.deleteEmp(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> UpdateEmp(@Valid @PathVariable Long id, @RequestBody EmpUpdateDTO dto){
        ApiResponse<String> response= adminService.updateEmp(id,dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("getAttendanceHistory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AttendanceHistoryDTO>>> GetAttendanceHistory(@RequestParam ("page") int page, @RequestParam ("size") int size){
        ApiResponse<Page<AttendanceHistoryDTO>> response= adminService.getAttHistory(page,size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getAttendanceHistory/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceHistoryDTO>> GetAttendanceHistoryById(@PathVariable Long id){
        ApiResponse<AttendanceHistoryDTO> response= adminService.getAttHistoryById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
