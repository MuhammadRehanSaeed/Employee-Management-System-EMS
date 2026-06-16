package com.rehancode.ems.Events;

import com.rehancode.ems.Model.AuditLog;
import com.rehancode.ems.Repository.AuditLogRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
public class AuditEventListener {

    private final AuditLogRepository auditLogRepository;


    public AuditEventListener(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }



    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleAudit(UserRegisteredEvent event) {


        System.out.println("AUDIT EVENT RECEIVED");


        AuditLog auditLog = new AuditLog();

        auditLog.setAction("USER_REGISTERED");

        auditLog.setDescription(
                "Admin registered new user"
        );

        auditLog.setPerformedBy(
                event.getPerformedBy()
        );

        auditLog.setEntityName(
                "UsersModel"
        );

        auditLog.setIpAddress(
                event.getIpAddress()
        );

        auditLog.setUserId(
                event.getId()
        );


        auditLogRepository.save(auditLog);


        System.out.println("AUDIT SAVED");
    }
}