package com.rehancode.ems.Util;

public class EmailTemplates {

    public static final String RESET_PASSWORD_SUBJECT =
            "EMS Portal - User Password Reset";

    public static String resetPasswordTemplate(
            String username,
            String tempPassword,
            String resetLink
    ) {

        return """
                Dear %s,

                Your temporary password is: %s

                Kindly change your password using the link below:
                %s

                Regards,
                EMS Team
                """.formatted(
                username,
                tempPassword,
                resetLink
        );
    }
}