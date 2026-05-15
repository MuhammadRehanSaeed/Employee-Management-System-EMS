package com.rehancode.ems.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rehancode.ems.Enum.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmpRequestDTO {

    @NotBlank(message = "EmployeeCode cannot be empty")
    private String employeeCode;
    @NotBlank(message = "Name cannot be empty")
    private String fullName;
    @NotBlank(message = "Phone Number cannot be empty")
    private String phoneNumber;
    @NotBlank(message = "department cannot be empty")
    private String department;
    @NotBlank(message = "designation cannot be empty")
    private String designation;
    @NotNull(message = "salary cannot be empty")
    private double salary;
    @NotNull(message = "status cannot be empty")
    private Status status;
    @NotBlank(message = "address cannot be empty")
    private String address;
    @NotNull(message = "Joining Date cannot be empty")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
    @NotNull(message = "UserId cannot be empty")
    private Long userId;



}
