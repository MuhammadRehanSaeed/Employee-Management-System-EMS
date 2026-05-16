package com.rehancode.ems.Controller;


import com.rehancode.ems.Dto.ChangePasswordDTO;
import com.rehancode.ems.Dto.EmpResponseDTO;
import com.rehancode.ems.Dto.EmpUpdateDTO;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService){
        this.employeeService=employeeService;
    }

    @GetMapping("me")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmpResponseDTO>> MyProfile(){
        ApiResponse<EmpResponseDTO> response= employeeService.getMyProfile();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("updateEmp")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<String>> UpdateProfile(@RequestBody EmpUpdateDTO empUpdateDTO){
        ApiResponse<String> response= employeeService.updateProfile(empUpdateDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("change-password")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordDTO Dto){
        ApiResponse<String> response= employeeService.changePassword(Dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
