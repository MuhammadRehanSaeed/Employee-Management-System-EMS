package com.rehancode.ems.Service.impl;

import com.rehancode.ems.Dto.EmpRequestDTO;
import com.rehancode.ems.Dto.EmpResponseDTO;
import com.rehancode.ems.Dto.MapStruct.EmployeeMapper;
import com.rehancode.ems.Dto.MapStruct.RegisterUserMapper;
import com.rehancode.ems.Dto.UserRequestDTO;
import com.rehancode.ems.Dto.UserResponseDTO;
import com.rehancode.ems.Enum.Role;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Exception.UserNotExists;
import com.rehancode.ems.Model.EmployeeModel;
import com.rehancode.ems.Model.UsersModel;
import com.rehancode.ems.Repository.EmpRepository;
import com.rehancode.ems.Repository.UserRepository;
import com.rehancode.ems.Service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminServiceImpl implements AdminService {

    private final BCryptPasswordEncoder encoder;
    private final RegisterUserMapper mapper;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;
    private final EmpRepository empRepository;
    public AdminServiceImpl(UserRepository userRepository,
                            BCryptPasswordEncoder encoder,
                            RegisterUserMapper mapper,EmployeeMapper employeeMapper,
                            EmpRepository empRepository
    ) {
        this.userRepository = userRepository;
        this.employeeMapper=employeeMapper;
        this.encoder = encoder;
        this.mapper = mapper;
        this.empRepository=empRepository;
    }

    @Override
    public ApiResponse<UserResponseDTO> registerUser(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new UserNotExists("username exists");
        }

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new UserNotExists("email exists");
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
            throw new UserNotExists("UserId already associated with a employee");
        }

        UsersModel user = userRepository.findById(empRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotExists("No user exists with this id"));
        if(user.getRole()!= Role.EMPLOYEE){
            throw new UserNotExists("Only employee profile can be created");
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
            throw new UserNotExists("Cannot access admin");

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
            throw new UserNotExists("Admin cannot be deleted");
        }

        userRepository.delete(user);
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("User Deleted Successfully")
                .data(null)
                .success(true)
                .build();
    }


}