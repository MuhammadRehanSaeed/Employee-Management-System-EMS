package com.rehancode.ems.Service;

import jakarta.validation.constraints.Email;

public interface EmailService {
    public void sendEmail(String to, String subject, String content);
}
