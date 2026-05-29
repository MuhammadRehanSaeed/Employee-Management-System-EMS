package com.rehancode.ems.Controller;


import com.rehancode.ems.Dto.ChangePasswordDTO;
import com.rehancode.ems.Dto.EmpResponseDTO;
import com.rehancode.ems.Dto.EmpUpdateDTO;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Service.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/employee")
@Slf4j
public class EmployeeController {
    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService){
        this.employeeService=employeeService;
    }

    @GetMapping("me")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmpResponseDTO>> MyProfile(){
        log.debug("GET /api/employee/me");
        ApiResponse<EmpResponseDTO> response= employeeService.getMyProfile();
        log.debug("GET /api/employee/me completed empId={}", response.getData().getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("updateEmp")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<String>> UpdateProfile(@RequestBody EmpUpdateDTO empUpdateDTO){
        log.info("PUT /api/employee/updateEmp");
        ApiResponse<String> response= employeeService.updateProfile(empUpdateDTO);
        log.info("PUT /api/employee/updateEmp completed – '{}'", response.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("change-password")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordDTO Dto){
        log.info("POST /api/employee/change-password");
        ApiResponse<String> response= employeeService.changePassword(Dto);
        log.info("POST /api/employee/change-password completed");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
