package com.rehancode.ems.Service.impl;

import com.rehancode.ems.Config.DetailsService.UserPrinicple;
import com.rehancode.ems.Dto.AttendanceHistoryDTO;
import com.rehancode.ems.Dto.MapStruct.AttendanceMapper;
import com.rehancode.ems.Enum.AttendanceStatus;
import com.rehancode.ems.Enum.Status;
import com.rehancode.ems.Exception.*;
import com.rehancode.ems.Model.AttendanceModel;
import com.rehancode.ems.Model.EmployeeModel;
import com.rehancode.ems.Repository.AttendanceRepository;
import com.rehancode.ems.Repository.EmpRepository;
import com.rehancode.ems.Service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private AttendanceMapper attendanceMapper;
    private EmpRepository empRepository;
    private AttendanceRepository attendanceRepository;
    public AttendanceServiceImpl(AttendanceMapper attendanceMapper,EmpRepository empRepository,AttendanceRepository attendanceRepository){
        this.empRepository=empRepository;
        this.attendanceMapper=attendanceMapper;
        this.attendanceRepository=attendanceRepository;
    }
    @Override
    public ApiResponse<String> checkIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth.getPrincipal().equals("anonymousUser")) {
            throw new UserNotAuthenticated("User not authenticated");
        }

        if (!(auth.getPrincipal() instanceof UserPrinicple userPrincipal)) {
            throw new UserNotAuthenticated("Invalid user principal");
        }

        Long userId = userPrincipal.getUser().getId();

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));


        AttendanceModel attendance=new AttendanceModel();
        attendance.setEmployee(emp);
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setCheckInTime(LocalDateTime.now());
        Optional<AttendanceModel> existingAttendance =
                attendanceRepository.findByEmployeeAndAttendanceDate(
                        emp,
                        LocalDate.now()
                );
        if(emp.getStatus()!= Status.ACTIVE){
            throw new AccessDeniedException("You are account is inactive cannot mark attendance");
        }
        if(existingAttendance.isPresent()){
            throw new CheckInExists("You have already Checked-In");
        }
        attendanceRepository.save(attendance);
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Welcome Check-In successfully")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<String> checkOut() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth.getPrincipal().equals("anonymousUser")) {
            throw new UserNotAuthenticated("User not authenticated");
        }

        if (!(auth.getPrincipal() instanceof UserPrinicple userPrincipal)) {
            throw new UserNotAuthenticated("Invalid user principal");
        }

        Long userId = userPrincipal.getUser().getId();

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));
        AttendanceModel attendance =
                attendanceRepository.findByEmployeeAndAttendanceDate(
                        emp,
                        LocalDate.now()
                ).orElseThrow(() ->
                        new CheckInExists("Check in first")
                );
        if(emp.getStatus()!= Status.ACTIVE){
            throw new AccessDeniedException("You are account is inactive cannot mark attendance");
        }
       if(attendance.getCheckOutTime()!=null){
           throw new CheckInExists("Already Checked Out");
       }
       attendance.setCheckOutTime(LocalDateTime.now());
       Long workingMinutes=Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).toMinutes();
       attendance.setTotalWorkingMinutes(workingMinutes);
       attendanceRepository.save(attendance);
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("GoodBye CheckOut successfully")
                .success(true)
                .build();

    }

    @Override
    public ApiResponse<AttendanceHistoryDTO> getTodayAttendance() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth.getPrincipal().equals("anonymousUser")) {
            throw new UserNotAuthenticated("User not authenticated");
        }

        if (!(auth.getPrincipal() instanceof UserPrinicple userPrincipal)) {
            throw new UserNotAuthenticated("Invalid user principal");
        }

        Long userId = userPrincipal.getUser().getId();

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));

        AttendanceModel model =attendanceRepository.findByEmployeeAndAttendanceDate(emp,LocalDate.now()).orElseThrow(()->new CheckInExists("No Attendance for today"));
        AttendanceHistoryDTO response=attendanceMapper.toDTO(model);
        return ApiResponse.<AttendanceHistoryDTO>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .message("Today's Attendance")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<List<AttendanceHistoryDTO>> getAttendanceHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth.getPrincipal().equals("anonymousUser")) {
            throw new UserNotAuthenticated("User not authenticated");
        }

        if (!(auth.getPrincipal() instanceof UserPrinicple userPrincipal)) {
            throw new UserNotAuthenticated("Invalid user principal");
        }

        Long userId = userPrincipal.getUser().getId();

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));

        List<AttendanceModel> model=attendanceRepository.findByEmployee_Id(emp.getId()).orElseThrow(()->new UserNotExists("No User Found"));
//        AttendanceHistoryDTO response=attendanceMapper.toDTO(model);
        List<AttendanceHistoryDTO> response =
                model.stream()
                        .map(attendanceMapper::toDTO)
                        .toList();

        return ApiResponse.<List<AttendanceHistoryDTO>>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .message("Attendance History")
                .success(true)
                .build();

    }
}
