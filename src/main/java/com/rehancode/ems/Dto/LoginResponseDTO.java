package com.rehancode.ems.Dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String username;
    private String email;
    private String token;
}
