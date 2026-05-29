package com.rehancode.ems.Controller;

import com.rehancode.ems.Dto.LeaveRequestDTO;
import com.rehancode.ems.Dto.LeaveResponseDTO;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Service.LeaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/leave")
public class LeaveController {
    private LeaveService leaveService;
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("apply-leave")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<String>> applyLeave(@RequestBody LeaveRequestDTO request) {
        log.info("POST /api/leave/apply-leave");
        ApiResponse<String> response=leaveService.applyLeave(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getLeaves")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<LeaveResponseDTO>>> getLeaves() {
        log.debug("GET /api/leave/getLeaves");
        ApiResponse<List<LeaveResponseDTO>> response=leaveService.getLeaves();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
