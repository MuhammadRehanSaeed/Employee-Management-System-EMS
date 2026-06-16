package com.rehancode.ems.Events;

import com.rehancode.ems.Service.EmailService;
import com.rehancode.ems.Util.EmailTemplates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailEventListener {
    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${password.change.path}")
    private String resetPath;


    private EmailService emailService;

    public EmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener
    public void sendEmail(UserRegisteredEvent event) {

        String resetLink = baseUrl + resetPath;
        String content = EmailTemplates.resetPasswordTemplate(event.getUser().getUsername(), event.getTempPassword(), resetLink);
        emailService.sendEmail(event.getEmail(), "EMS Portal - User Password Reset",content);
    }


}
