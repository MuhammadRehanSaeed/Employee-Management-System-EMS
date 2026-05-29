package com.rehancode.ems.Controller;

import com.rehancode.ems.Dto.*;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Service.AdminService;
import com.rehancode.ems.Service.LeaveService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin")
public class AdminController {
    private LeaveService leaveService;
    private AdminService adminService;
    public AdminController(AdminService adminService, LeaveService leaveService){
        this.adminService = adminService;
        this.leaveService = leaveService;
    }

    @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerEmp(@Valid @RequestBody UserRequestDTO userRequestDTO){
        log.info("POST /api/admin/create username='{}'", userRequestDTO.getUsername());
        ApiResponse<UserResponseDTO> response = adminService.registerUser(userRequestDTO);
        log.info("POST /api/admin/create completed userId={}", response.getData().getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("createEmp")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmpResponseDTO>> registerEmp(@Valid @RequestBody EmpRequestDTO empRequestDTO){
        log.info("POST /api/admin/createEmp userId={}", empRequestDTO.getUserId());
        ApiResponse<EmpResponseDTO> response = adminService.createEmployee(empRequestDTO);
        log.info("POST /api/admin/createEmp completed empId={}", response.getData().getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("getUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser(@Valid @PathVariable Long id){
        log.debug("GET /api/admin/getUser/{}", id);
        ApiResponse<UserResponseDTO> response = adminService.getUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@Valid @PathVariable Long id){
        log.info("DELETE /api/admin/deleteUser/{}", id);
        ApiResponse<String> response = adminService.deleteUser(id);
        log.info("DELETE /api/admin/deleteUser/{} completed", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getUsers(@RequestParam("page") int page, @RequestParam("size") int size){
        log.debug("GET /api/admin/users page={} size={}", page, size);
        ApiResponse<Page<UserResponseDTO>> response = adminService.getAllUsers(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<EmpResponseDTO>>> getAllEmp(@RequestParam("page") int page, @RequestParam("size") int size){
        log.debug("GET /api/admin/employees page={} size={}", page, size);
        ApiResponse<Page<EmpResponseDTO>> response = adminService.getAllEmp(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getEmp/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmpResponseDTO>> getEmp(@Valid @PathVariable Long id){
        log.debug("GET /api/admin/getEmp/{}", id);
        ApiResponse<EmpResponseDTO> response = adminService.getEmp(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("deleteEmp/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteEmp(@Valid @PathVariable Long id){
        log.info("DELETE /api/admin/deleteEmp/{}", id);
        ApiResponse<String> response = adminService.deleteEmp(id);
        log.info("DELETE /api/admin/deleteEmp/{} completed", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> UpdateEmp(@Valid @PathVariable Long id, @RequestBody EmpUpdateDTO dto){
        log.info("PUT /api/admin/update/{}", id);
        ApiResponse<String> response = adminService.updateEmp(id, dto);
        log.info("PUT /api/admin/update/{} completed – '{}'", id, response.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getAttendanceHistory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AttendanceHistoryDTO>>> GetAttendanceHistory(@RequestParam("page") int page, @RequestParam("size") int size){
        log.debug("GET /api/admin/getAttendanceHistory page={} size={}", page, size);
        ApiResponse<Page<AttendanceHistoryDTO>> response = adminService.getAttHistory(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getAttendanceHistory/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceHistoryDTO>> GetAttendanceHistoryById(@PathVariable Long id){
        log.debug("GET /api/admin/getAttendanceHistory/{}", id);
        ApiResponse<AttendanceHistoryDTO> response = adminService.getAttHistoryById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AttendanceReportDTO>>> getAttendanceReport(@RequestParam String date){
        log.info("GET /api/admin/report date='{}'", date);
        ApiResponse<List<AttendanceReportDTO>> response = adminService.getReport(date);
        log.info("GET /api/admin/report date='{}' returned {} records", date, response.getData().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getAdminLeaves")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveResponseDTO>>> getLeave(){
        log.debug("GET /api/admin/getAdminLeaves");
        ApiResponse<List<LeaveResponseDTO>> response = leaveService.getLeave();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getAdminLeaves/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveResponseDTO>>> getLeave(@PathVariable Long id){
        log.debug("GET /api/admin/getAdminLeaves/{}", id);
        ApiResponse<List<LeaveResponseDTO>> response = leaveService.getLeaveById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("getAdminLeaves/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> approveLeave(@PathVariable Long id){
        log.info("POST /api/admin/getAdminLeaves/{}/approve", id);
        ApiResponse<String> response = leaveService.approveLeave(id);
        log.info("Leave approval result leaveId={} status='{}'", id, response.getData());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("getAdminLeaves/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> rejectLeave(@PathVariable Long id){
        log.info("POST /api/admin/getAdminLeaves/{}/reject", id);
        ApiResponse<String> response = leaveService.rejectLeave(id);
        log.info("Leave rejection result leaveId={} status='{}'", id, response.getData());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
