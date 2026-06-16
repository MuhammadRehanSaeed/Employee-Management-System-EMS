package com.rehancode.ems.Repository;

import com.rehancode.ems.Events.UserRegisteredEvent;
import com.rehancode.ems.Model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
}
