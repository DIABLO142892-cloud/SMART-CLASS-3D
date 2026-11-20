package com.smartclass.dao;

import java.util.List;
import com.smartclass.model.AuditLog;

public interface AuditLogDAO {
    List<AuditLog> findAll();
    List<AuditLog> findByUserId(Integer userId);
    List<AuditLog> findByEntityType(String entityType);
    AuditLog create(AuditLog auditLog);
}