package com.rehancode.ems.Service.impl;

import com.rehancode.ems.Config.Jwt.JwtService;
import com.rehancode.ems.Dto.*;
import com.rehancode.ems.Dto.MapStruct.AttendanceMapper;
import com.rehancode.ems.Dto.MapStruct.EmployeeMapper;
import com.rehancode.ems.Dto.MapStruct.RegisterUserMapper;
import com.rehancode.ems.Enum.Role;
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
import io.jsonwebtoken.Jwt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AdminServiceImpl implements AdminService {

    private final BCryptPasswordEncoder encoder;
    private final AttendanceRepository attendanceRepository;
    private final JwtService jwtService;
    private final AttendanceMapper attendanceMapper;
    private final RegisterUserMapper mapper;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;
    private final EmpRepository empRepository;
    public AdminServiceImpl(AttendanceMapper attendanceMapper,AttendanceRepository attendanceRepository,JwtService jwtService,UserRepository userRepository,
                            BCryptPasswordEncoder encoder,
                            RegisterUserMapper mapper,EmployeeMapper employeeMapper,
                            EmpRepository empRepository
    ) {
        this.userRepository = userRepository;
        this.attendanceRepository=attendanceRepository;
        this.employeeMapper=employeeMapper;
        this.attendanceMapper=attendanceMapper;
        this.jwtService=jwtService;
        this.encoder = encoder;
        this.mapper = mapper;
        this.empRepository=empRepository;
    }

    @Override
    public ApiResponse<UserResponseDTO> registerUser(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new UserExistsAlready("username exists");
        }

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new UserExistsAlready("email exists");
        }

        UsersModel user = mapper.mapToEntity(userRequestDTO);

        user.setPassword(encoder.encode(userRequestDTO.getPassword()));

        user.setActive(
                userRequestDTO.getIsActive() != null
                        ? userRequestDTO.getIsActive()
                        : true
        );

        UsersModel savedUser = userRepository.save(user);

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
        if(empRepository.existsByUser_Id(empRequestDTO.getUserId())){
            throw new UserExistsAlready("UserId already associated with a employee");
        }

        UsersModel user = userRepository.findById(empRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotExists("No user exists with this id"));
        if(user.getRole()!= Role.EMPLOYEE){
            throw new AccessDeniedException("Only employee profile can be created");
        }
        EmployeeModel emp=employeeMapper.toEntity(empRequestDTO);
        emp.setUser(user);

        EmployeeModel savedEmp=empRepository.save(emp);
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

        UsersModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotExists("No user exists with this id"));
        if(user.getRole()!=Role.EMPLOYEE){
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
        UsersModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotExists("No user exists with this id"));

        if(user.getRole()!=Role.EMPLOYEE){
            throw new AccessDeniedException("Admin cannot be deleted");
        }

        userRepository.delete(user);
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("User Deleted Successfully")
                .data(null)
                .success(true)
                .build();
    }


    @Override
    public ApiResponse<Page<UserResponseDTO>> getAllUsers(int page, int size) {
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
        EmployeeModel emp=empRepository.findById(id).orElseThrow(()->new UserNotExists("Employee Doesn't Exists"));
        UsersModel user = emp.getUser();
        if (user != null) {
            user.setEmployee(null);
            userRepository.save(user);
        }
        empRepository.deleteById(id);
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Employee Deleted Successfully")
                .data(null)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<String> updateEmp(Long id, EmpUpdateDTO dto) {



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
        AttendanceModel model=attendanceRepository.findByemployee_Id(id).orElseThrow(()-> new UserNotExists("No User Exists"));

        AttendanceHistoryDTO response=attendanceMapper.toDTO(model);
        return ApiResponse.<AttendanceHistoryDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Employee Attendance History")
                .data(response)
                .success(true)
                .build();

    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }


}