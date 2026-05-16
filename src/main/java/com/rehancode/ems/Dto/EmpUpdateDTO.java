package com.rehancode.ems.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rehancode.ems.Enum.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmpUpdateDTO {
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
}
