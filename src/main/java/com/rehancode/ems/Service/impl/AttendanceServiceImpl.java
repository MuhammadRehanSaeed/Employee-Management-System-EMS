package com.rehancode.ems.Service.impl;

import com.rehancode.ems.Config.DetailsService.UserPrinicple;
import com.rehancode.ems.Constants.Constants;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.rehancode.ems.Constants.Constants.HALF_DAY_MINUTES;
import static com.rehancode.ems.Constants.Constants.OFFICE_START_TIME;

@Slf4j
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
        log.info("Check-in attempt userId={}", userId);

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));

        if(emp.getStatus()!= Status.ACTIVE){
            log.warn("Check-in denied – employee inactive empId={} userId={}", emp.getId(), userId);
            throw new AccessDeniedException("You are account is inactive cannot mark attendance");
        }

        Optional<AttendanceModel> existingAttendance =
                attendanceRepository.findByEmployeeAndAttendanceDate(emp, LocalDate.now());
        if(existingAttendance.isPresent()){
            log.warn("Check-in denied – already checked in empId={}", emp.getId());
            throw new CheckInExists("You have already Checked-In");
        }

        AttendanceModel attendance=new AttendanceModel();
        attendance.setEmployee(emp);
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setCheckInTime(LocalDateTime.now());
        LocalTime checkInTime = attendance.getCheckInTime().toLocalTime();
        attendance.setStatus(AttendanceStatus.PRESENT);

        if (checkInTime.isAfter(Constants.OFFICE_START_TIME)) {
            attendance.setLate(true);
            log.info("Late check-in recorded empId={} checkInTime={}", emp.getId(), checkInTime);
        }

        attendanceRepository.save(attendance);
        log.info("Check-in successful empId={} date={}", emp.getId(), LocalDate.now());
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
        log.info("Check-out attempt userId={}", userId);

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));

        if(emp.getStatus()!= Status.ACTIVE){
            log.warn("Check-out denied – employee inactive empId={}", emp.getId());
            throw new AccessDeniedException("You are account is inactive cannot mark attendance");
        }

        AttendanceModel attendance =
                attendanceRepository.findByEmployeeAndAttendanceDate(emp, LocalDate.now())
                        .orElseThrow(() -> new CheckInExists("Check in first"));

       if(attendance.getCheckOutTime()!=null){
           log.warn("Check-out denied – already checked out empId={}", emp.getId());
           throw new CheckInExists("Already Checked Out");
       }
       attendance.setCheckOutTime(LocalDateTime.now());
       Long workingMinutes=Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).toMinutes();
       if(workingMinutes<HALF_DAY_MINUTES){
           attendance.setStatus(AttendanceStatus.HALF_DAY);
           log.info("Half-day recorded empId={} workingMinutes={}", emp.getId(), workingMinutes);
       }
       attendance.setTotalWorkingMinutes(workingMinutes);
       attendanceRepository.save(attendance);
       log.info("Check-out successful empId={} workingMinutes={}", emp.getId(), workingMinutes);
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
        log.debug("Fetching today's attendance userId={}", userId);

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
        log.debug("Fetching attendance history userId={}", userId);

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));

        List<AttendanceModel> model=attendanceRepository.findByEmployee_Id(emp.getId()).orElseThrow(()->new UserNotExists("No User Found"));
        List<AttendanceHistoryDTO> response =
                model.stream()
                        .map(attendanceMapper::toDTO)
                        .toList();

        log.debug("Attendance history fetched empId={} records={}", emp.getId(), response.size());
        return ApiResponse.<List<AttendanceHistoryDTO>>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .message("Attendance History")
                .success(true)
                .build();
    }
}
