package com.rehancode.ems.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;
    @NotBlank(message = "New password cannot be empty")
    private String newPassword;
    @NotBlank(message = "password cannot be empty")
    private String ReEnterPassword;
}
