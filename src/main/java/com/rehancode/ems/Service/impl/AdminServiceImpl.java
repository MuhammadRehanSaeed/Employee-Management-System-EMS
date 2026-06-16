package com.rehancode.ems.Service.impl;

import com.rehancode.ems.Config.Jwt.JwtService;
import com.rehancode.ems.Dto.*;
import com.rehancode.ems.Dto.MapStruct.AttendanceMapper;
import com.rehancode.ems.Dto.MapStruct.EmployeeMapper;
import com.rehancode.ems.Dto.MapStruct.RegisterUserMapper;
import com.rehancode.ems.Enum.AttendanceStatus;
import com.rehancode.ems.Enum.Role;
import com.rehancode.ems.Events.UserRegisteredEvent;
import com.rehancode.ems.Exception.AccessDeniedException;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Exception.UserExistsAlready;
import com.rehancode.ems.Exception.UserNotExists;
import com.rehancode.ems.Model.AttendanceModel;
import com.rehancode.ems.Model.EmployeeModel;
import com.rehancode.ems.Model.UsersModel;
import com.rehancode.ems.Repository.AttendanceRepository;
import com.rehancode.ems.Repository.EmpRepository;
import com.rehancode.ems.Repository.UserRepository;
import com.rehancode.ems.Service.AdminService;
import com.rehancode.ems.Service.EmailService;
import com.rehancode.ems.Util.EmailTemplates;
import com.rehancode.ems.Util.Util;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AdminServiceImpl implements AdminService {


    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${password.change.path}")
    private String resetPath;
    private final EmailService  emailService;
    private final BCryptPasswordEncoder encoder;
    private final AttendanceRepository attendanceRepository;
    private final JwtService jwtService;
    private final AttendanceMapper attendanceMapper;
    private final RegisterUserMapper mapper;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;
    private final EmpRepository empRepository;
    private final ApplicationEventPublisher publisher;
    public AdminServiceImpl(EmailService  emailService,AttendanceMapper attendanceMapper,AttendanceRepository attendanceRepository,JwtService jwtService,UserRepository userRepository,
                            BCryptPasswordEncoder encoder,
                            RegisterUserMapper mapper,EmployeeMapper employeeMapper,
                            EmpRepository empRepository,
                            ApplicationEventPublisher publisher
    ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.attendanceRepository=attendanceRepository;
        this.employeeMapper=employeeMapper;
        this.attendanceMapper=attendanceMapper;
        this.jwtService=jwtService;
        this.encoder = encoder;
        this.mapper = mapper;
        this.empRepository=empRepository;
        this.publisher=publisher;
    }


    @Override
    @Transactional
    public ApiResponse<UserResponseDTO> registerUser(UserRequestDTO userRequestDTO,String ip) {
        log.info("Registering new user username='{}'", userRequestDTO.getUsername());

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            log.warn("Registration failed – username='{}' already exists", userRequestDTO.getUsername());
            throw new UserExistsAlready("username exists");
        }

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Registration failed – email='{}' already exists", userRequestDTO.getEmail());
            throw new UserExistsAlready("email exists");
        }


        UsersModel user = mapper.mapToEntity(userRequestDTO);
        String tempPassword= Util.generateTempPassword();
        user.setPassword(encoder.encode(tempPassword));
       // String resetLink="http://localhost:8080/api/employee/change-password";

        String resetLink = baseUrl + resetPath;



//        user.setPassword(encoder.encode(userRequestDTO.getPassword()));
        user.setActive(
                userRequestDTO.getIsActive() != null
                        ? userRequestDTO.getIsActive()
                        : true
        );
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(480);

        UsersModel savedUser = userRepository.save(user);
        log.info("User registered successfully userId={} username='{}'", savedUser.getId(), savedUser.getUsername());

        String content = EmailTemplates.resetPasswordTemplate(user.getUsername(), tempPassword, resetLink);
        log.info("Sending welcome email to='{}' subject='EMS Portal - User Password Reset'", user.getEmail());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        publisher.publishEvent(new UserRegisteredEvent(savedUser.getId(),savedUser.getEmail(),savedUser,tempPassword,username,ip));
//        emailService.sendEmail(
//                user.getEmail(),
//                "EMS Portal - User Password Reset",
//                content
//        );
        log.debug("Welcome email dispatched (async) to='{}'", user.getEmail());
        UserResponseDTO response = mapper.mapToDto(savedUser);
        return ApiResponse.<UserResponseDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("User Created Successfully")
                .data(response)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<EmpResponseDTO> createEmployee(EmpRequestDTO empRequestDTO) {
        log.info("Creating employee profile for userId={}", empRequestDTO.getUserId());
        if(empRepository.existsByUser_Id(empRequestDTO.getUserId())){
            log.warn("Employee creation failed – userId={} already has an employee profile", empRequestDTO.getUserId());
            throw new UserExistsAlready("UserId already associated with a employee");
        }

        UsersModel user = userRepository.findById(empRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotExists("No user exists with this id"));
        if(user.getRole()!= Role.EMPLOYEE){
            log.warn("Employee creation denied – userId={} has role={}", user.getId(), user.getRole());
            throw new AccessDeniedException("Only employee profile can be created");
        }
        EmployeeModel emp=employeeMapper.toEntity(empRequestDTO);
        emp.setUser(user);

        EmployeeModel savedEmp=empRepository.save(emp);
        log.info("Employee created successfully empId={} userId={}", savedEmp.getId(), user.getId());
        EmpResponseDTO response=employeeMapper.toDTO(savedEmp);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());

        return ApiResponse.<EmpResponseDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Employee Created Successfully")
                .data(response)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<UserResponseDTO> getUser(Long id) {
        log.debug("Fetching user userId={}", id);
        UsersModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotExists("No user exists with this id"));
        if(user.getRole()!=Role.EMPLOYEE){
            log.warn("Access denied – userId={} is not an EMPLOYEE", id);
            throw new AccessDeniedException("Cannot access admin");
        }
        UserResponseDTO response=mapper.mapToDto(user);
        return ApiResponse.<UserResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("User Fetched Successfully")
                .data(response)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<String> deleteUser(Long id) {
        log.info("Deleting user userId={}", id);
        UsersModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotExists("No user exists with this id"));

        if(user.getRole()!=Role.EMPLOYEE){
            log.warn("Delete denied – userId={} is not an EMPLOYEE", id);
            throw new AccessDeniedException("Admin cannot be deleted");
        }

        userRepository.delete(user);
        log.info("User deleted successfully userId={}", id);
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("User Deleted Successfully")
                .data(null)
                .success(true)
                .build();
    }


    @Override
    public ApiResponse<Page<UserResponseDTO>> getAllUsers(int page, int size) {
        log.debug("Fetching all users page={} size={}", page, size);
        Pageable pageable= PageRequest.of(page,size);
        Page<UsersModel> users=userRepository.findAll(pageable);
        Page<UserResponseDTO> response=users.map(mapper::mapToDto);
        return ApiResponse.<Page<UserResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Users fetched Successfully")
                .data(response)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<Page<EmpResponseDTO>> getAllEmp(int page, int size) {
        log.debug("Fetching all employees page={} size={}", page, size);
        Pageable pageable= PageRequest.of(page,size);
        Page<EmployeeModel> users=empRepository.findAll(pageable);
        Page<EmpResponseDTO> response=users.map(employeeMapper::toDTO);
        return ApiResponse.<Page<EmpResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Employees fetched Successfully")
                .data(response)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<EmpResponseDTO> getEmp(Long id) {
        log.debug("Fetching employee empId={}", id);
        EmployeeModel emp=empRepository.findById(id).orElseThrow(()->new UserNotExists("Employee Doesn't Exists"));
        EmpResponseDTO responseDTO=employeeMapper.toDTO(emp);
        return ApiResponse.<EmpResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Employee fetched Successfully")
                .data(responseDTO)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<String> deleteEmp(Long id) {
        log.info("Deleting employee empId={}", id);
        EmployeeModel emp=empRepository.findById(id).orElseThrow(()->new UserNotExists("Employee Doesn't Exists"));
        UsersModel user = emp.getUser();
        if (user != null) {
            user.setEmployee(null);
            userRepository.save(user);
        }
        empRepository.deleteById(id);
        log.info("Employee deleted successfully empId={}", id);
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Employee Deleted Successfully")
                .data(null)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<String> updateEmp(Long id, EmpUpdateDTO dto) {
        log.info("Updating employee empId={}", id);

        EmployeeModel emp = empRepository.findById(id)
                .orElseThrow(() -> new UserNotExists("Employee Doesn't Exists"));

        boolean updated = false;

        // FULL NAME
        String fullName = normalize(dto.getFullName());
        if (fullName != null && !fullName.equals(emp.getFullName())) {
            emp.setFullName(fullName);
            updated = true;
        }

        // PHONE
        String phone = normalize(dto.getPhoneNumber());
        if (phone != null && !phone.equals(emp.getPhoneNumber())) {
            emp.setPhoneNumber(phone);
            updated = true;
        }

        // DEPARTMENT
        String department = normalize(dto.getDepartment());
        if (department != null && !department.equals(emp.getDepartment())) {
            emp.setDepartment(department);
            updated = true;
        }

        // DESIGNATION
        String designation = normalize(dto.getDesignation());
        if (designation != null && !designation.equals(emp.getDesignation())) {
            emp.setDesignation(designation);
            updated = true;
        }

        // SALARY
        if (dto.getSalary() > 0 && dto.getSalary() != emp.getSalary()) {
            emp.setSalary(dto.getSalary());
            updated = true;
        }

        // STATUS
        if (dto.getStatus() != null && dto.getStatus() != emp.getStatus()) {
            emp.setStatus(dto.getStatus());
            updated = true;
        }

        // ADDRESS
        String address = normalize(dto.getAddress());
        if (address != null && !address.equals(emp.getAddress())) {
            emp.setAddress(address);
            updated = true;
        }

        if (updated) {
            empRepository.save(emp);
            log.info("Employee updated empId={}", id);
        } else {
            log.debug("No changes detected for empId={}", id);
        }

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message(updated ? "Employee updated successfully" : "No changes detected")
                .data(null)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<Page<AttendanceHistoryDTO>> getAttHistory(int page,int size) {
        log.debug("Fetching all attendance history page={} size={}", page, size);
        Pageable pageable=PageRequest.of(page,size);
        Page<AttendanceModel> attendanceModel=attendanceRepository.findAll(pageable);
        Page<AttendanceHistoryDTO> response=attendanceModel.map(attendanceMapper::toDTO);

        return ApiResponse.<Page<AttendanceHistoryDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Employee Attendance History")
                .data(response)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<AttendanceHistoryDTO> getAttHistoryById(Long id) {
        log.debug("Fetching attendance history for empId={}", id);
        AttendanceModel model=attendanceRepository.findByemployee_Id(id).orElseThrow(()-> new UserNotExists("No User Exists"));
        AttendanceHistoryDTO response=attendanceMapper.toDTO(model);
        return ApiResponse.<AttendanceHistoryDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Employee Attendance History")
                .data(response)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<List<AttendanceReportDTO>> getReport(String date) {
        log.info("Generating attendance report for date='{}'", date);

        LocalDate parsedDate = LocalDate.parse(date);

        List<EmployeeModel> employees = empRepository.findAll();
        log.debug("Total employees found for report count={}", employees.size());

        List<AttendanceModel> attendanceList =
                attendanceRepository.findByAttendanceDate(parsedDate);
        log.debug("Attendance records found for date='{}' count={}", parsedDate, attendanceList.size());

        Map<Long, AttendanceModel> attendanceMap =
                attendanceList.stream()
                        .collect(Collectors.toMap(
                                a -> a.getEmployee().getId(),
                                a -> a
                        ));

        List<AttendanceReportDTO> report = new ArrayList<>();

        for (EmployeeModel emp : employees) {

            AttendanceModel att = attendanceMap.get(emp.getId());

            AttendanceReportDTO dto = new AttendanceReportDTO();

            dto.setEmployeeId(emp.getId());
            dto.setEmployeeName(emp.getFullName());
            dto.setDesignation(emp.getDesignation());
            dto.setAttendanceDate(parsedDate);

            if (att == null) {
                dto.setStatus(AttendanceStatus.ABSENT);
                dto.setCheckedIn(false);
                dto.setCheckedOut(false);
                log.debug("Employee marked ABSENT empId={} date='{}'", emp.getId(), parsedDate);
            } else {
                dto.setCheckInTime(att.getCheckInTime());
                dto.setCheckOutTime(att.getCheckOutTime());
                dto.setCheckedIn(att.getCheckInTime() != null);
                dto.setCheckedOut(att.getCheckOutTime() != null);
                dto.setTotalWorkingMinutes(att.getTotalWorkingMinutes());
                dto.setStatus(att.getStatus());
                log.debug("Employee attendance empId={} status='{}' checkedIn={} checkedOut={}",
                        emp.getId(), att.getStatus(), att.getCheckInTime() != null, att.getCheckOutTime() != null);
            }

            report.add(dto);
        }

        log.info("Attendance report generated date='{}' totalEmployees={} present={} absent={}",
                parsedDate,
                report.size(),
                report.stream().filter(r -> r.getStatus() != AttendanceStatus.ABSENT).count(),
                report.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count());

        return ApiResponse.<List<AttendanceReportDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Attendance Report")
                .data(report)
                .success(true)
                .build();
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }


}