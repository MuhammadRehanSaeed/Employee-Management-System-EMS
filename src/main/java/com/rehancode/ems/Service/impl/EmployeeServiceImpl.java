package com.rehancode.ems.Service.impl;


import com.rehancode.ems.Config.DetailsService.UserPrinicple;
import com.rehancode.ems.Dto.ChangePasswordDTO;
import com.rehancode.ems.Dto.EmpResponseDTO;
import com.rehancode.ems.Dto.EmpUpdateDTO;
import com.rehancode.ems.Dto.MapStruct.EmployeeMapper;
import com.rehancode.ems.Exception.*;
import com.rehancode.ems.Model.EmployeeModel;
import com.rehancode.ems.Model.UsersModel;
import com.rehancode.ems.Repository.EmpRepository;
import com.rehancode.ems.Repository.UserRepository;
import com.rehancode.ems.Service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private BCryptPasswordEncoder encoder;
    private EmployeeMapper mapper;
    private EmpRepository empRepository;
    private UserRepository userRepository;
    public EmployeeServiceImpl(UserRepository userRepository,EmployeeMapper mapper,EmpRepository empRepository,BCryptPasswordEncoder encoder){
        this.empRepository=empRepository;
        this.userRepository=userRepository;
        this.mapper=mapper;
        this.encoder=encoder;
    }

    @Override
    public ApiResponse<EmpResponseDTO> getMyProfile() {

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
        log.debug("Fetching profile for userId={}", userId);

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));

        return ApiResponse.<EmpResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("User fetched successfully")
                .data(mapper.toDTO(emp))
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<String> updateProfile(EmpUpdateDTO dto) {
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
        log.info("Profile update attempt userId={}", userId);

        EmployeeModel emp = empRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserNotExists("Employee not found"));
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
        if (dto.getStatus() != null && !dto.getStatus().equals(emp.getStatus())) {
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
            log.info("Profile updated userId={}", userId);
        } else {
            log.debug("No profile changes detected userId={}", userId);
        }

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message(updated ? "Employee updated successfully" : "No changes detected")
                .data(null)
                .success(true)
                .build();

    }

    @Override
    public ApiResponse<String> changePassword(ChangePasswordDTO dto) {

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
        log.info("Password change attempt userId={}", userId);

        UsersModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExists("User not found"));


        // 1. Check new password match
        if (!dto.getNewPassword().equals(dto.getReEnterPassword())) {
            log.warn("Password change failed – passwords do not match userId={}", userId);
            throw new InvalidCredentials("Passwords do not match");
        }

        // 2. Check current password (IMPORTANT SECURITY STEP)
        if (!encoder.matches(dto.getOldPassword(), user.getPassword())) {
            log.warn("Password change failed – old password incorrect userId={}", userId);
            throw new InvalidCredentials("Old password is incorrect");
        }

        // 3. Prevent same password reuse
        if (encoder.matches(dto.getNewPassword(), user.getPassword())) {
            log.warn("Password change failed – new password same as old userId={}", userId);
            throw new InvalidCredentials("New password cannot be same as old password");
        }

        // 4. Update password
        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully userId={}", userId);


        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Password changed successfully")
                .success(true)
                .build();
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
