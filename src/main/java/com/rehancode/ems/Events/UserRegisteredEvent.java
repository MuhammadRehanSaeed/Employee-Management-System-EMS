package com.rehancode.ems.Events;


import com.rehancode.ems.Model.UsersModel;
import lombok.Data;

@Data
public class UserRegisteredEvent {
    private Long id;
    private String email;
    private UsersModel user;
    private String tempPassword;
    private String performedBy;
    private String ipAddress;

    public UserRegisteredEvent(Long id, String email,UsersModel user,String tempPassword,String performedBy,String ipAddress) {
        this.id = id;
        this.email = email;
        this.tempPassword=tempPassword;
        this.user = user;
        this.performedBy=performedBy;
        this.ipAddress = ipAddress;
    }
}
