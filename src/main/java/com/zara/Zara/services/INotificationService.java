package com.zara.Zara.services;

import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface INotificationService {

    Notification save(Notification notification);

    Page<Notification> findByBusiness(Long businessId, Pageable pageable);
    Page<Notification> findByCustomer(Long customerId, Pageable pageable);
    Page<Agent> findByAgent(Long agentId, Pageable pageable);
    Page<Notification> findByAdmin(Long adminId, Pageable pageable);
    Page<Notification> findByDeveloper(Long developerId, Pageable pageable);
}
