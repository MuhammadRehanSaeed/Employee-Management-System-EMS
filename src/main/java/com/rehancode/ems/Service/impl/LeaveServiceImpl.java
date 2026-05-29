package com.rehancode.ems.Service.impl;

import com.rehancode.ems.Dto.LeaveRequestDTO;
import com.rehancode.ems.Dto.LeaveResponseDTO;
import com.rehancode.ems.Dto.MapStruct.LeaveMapper;
import com.rehancode.ems.Enum.LeaveStatus;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Exception.InvalidLeaveException;
import com.rehancode.ems.Exception.UserNotExists;
import com.rehancode.ems.Model.EmployeeModel;
import com.rehancode.ems.Model.LeaveModel;
import com.rehancode.ems.Repository.EmpRepository;
import com.rehancode.ems.Repository.LeaveRepository;
import com.rehancode.ems.Service.LeaveService;
import com.rehancode.ems.Util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class LeaveServiceImpl implements LeaveService {
    private EmpRepository empRepository;
    private LeaveMapper leaveMapper;
    private LeaveRepository  leaveRepository;
    public LeaveServiceImpl(EmpRepository empRepository, LeaveMapper leaveMapper, LeaveRepository leaveRepository) {
        this.empRepository = empRepository;
        this.leaveMapper = leaveMapper;
        this.leaveRepository = leaveRepository;
    }
    @Override
    public ApiResponse<String> applyLeave(LeaveRequestDTO request) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        log.info("Leave application attempt userId={} type='{}' from='{}' to='{}'",
                userId, request.getLeaveType(), request.getStartDate(), request.getEndDate());

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (endDate.isBefore(startDate)) {
            log.warn("Leave rejected – end date before start date empId={}", emp.getId());
            throw new InvalidLeaveException("End date cannot be before start date");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        LeaveModel leave = leaveMapper.leaveDtoToLeave(request);
        leave.setDays(days);
        leave.setLeaveStatus(LeaveStatus.PENDING);
        leave.setEmployee(emp);

        leaveRepository.save(leave);
        log.info("Leave applied successfully empId={} days={} status=PENDING", emp.getId(), days);

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Leave applied successfully")
                .data(null)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<List<LeaveResponseDTO>> getLeaves() {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        log.debug("Fetching leave history for userId={}", userId);

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));

        List<LeaveModel> model=leaveRepository.findByEmployee_Id(emp.getId());
        if(model.isEmpty()){
            log.warn("No leave records found for empId={}", emp.getId());
            throw new UserNotExists("Leave not found");
        }

        List<LeaveResponseDTO> response =
                model.stream()
                        .map(leaveMapper::leaveToLeaveResponseDTO)
                        .toList();

        log.debug("Leave history fetched empId={} records={}", emp.getId(), response.size());
        return ApiResponse.<List<LeaveResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .message("Leave History")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<List<LeaveResponseDTO>> getLeave() {
        log.debug("Admin fetching all leave records");
        List<LeaveModel> model=leaveRepository.findAll();
        List<LeaveResponseDTO> response=model.stream()
                .map(leaveMapper::leaveToLeaveResponseDTO)
                .toList();
        log.debug("All leave records fetched count={}", response.size());
        return ApiResponse.<List<LeaveResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .message("Leave History")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<List<LeaveResponseDTO>> getLeaveById(Long id) {
        log.debug("Fetching leave records for empId={}", id);
        List<LeaveModel> model=leaveRepository.findByEmployee_Id(id);
        if(model.isEmpty()){
            log.warn("No leave records found for empId={}", id);
            throw new UserNotExists("Leave not found");
        }
        List<LeaveResponseDTO> response=model.stream()
                .map(leaveMapper::leaveToLeaveResponseDTO)
                .toList();
        return ApiResponse.<List<LeaveResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .message("Leave History")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<String> approveLeave(Long id) {
        log.info("Approving leave leaveId={}", id);
        LeaveModel model = leaveRepository.findById(id)
                .orElseThrow(() -> new UserNotExists("Leave not found"));

        if (model.getLeaveStatus() != LeaveStatus.PENDING) {
            log.warn("Leave approval skipped – already processed leaveId={} status='{}'",
                    id, model.getLeaveStatus());
            return ApiResponse.<String>builder()
                    .status(HttpStatus.CONFLICT.value())
                    .message("Leave already processed")
                    .success(false)
                    .data(model.getLeaveStatus().toString())
                    .build();
        }

        model.setLeaveStatus(LeaveStatus.APPROVED);
        leaveRepository.save(model);
        log.info("Leave approved leaveId={} empId={}", id, model.getEmployee().getId());

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Leave approved successfully")
                .success(true)
                .data("APPROVED")
                .build();
    }

    @Override
    public ApiResponse<String> rejectLeave(Long id) {
        log.info("Rejecting leave leaveId={}", id);
        LeaveModel model = leaveRepository.findById(id)
                .orElseThrow(() -> new UserNotExists("Leave not found"));

        if (model.getLeaveStatus() != LeaveStatus.PENDING) {
            log.warn("Leave rejection skipped – already processed leaveId={} status='{}'",
                    id, model.getLeaveStatus());
            return ApiResponse.<String>builder()
                    .status(HttpStatus.CONFLICT.value())
                    .message("Leave already processed")
                    .success(false)
                    .data(model.getLeaveStatus().toString())
                    .build();
        }

        model.setLeaveStatus(LeaveStatus.REJECTED);
        leaveRepository.save(model);
        log.info("Leave rejected leaveId={} empId={}", id, model.getEmployee().getId());

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Leave Rejected successfully")
                .success(true)
                .data("Rejected")
                .build();
    }

}
