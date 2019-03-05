package com.zara.Zara.services;

import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Notification;
import com.zara.Zara.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImp implements INotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Page<Notification> findByBusiness(Long businessId, Pageable pageable) {
        return notificationRepository.findByBusiness(businessId, pageable);
    }

    @Override
    public Page<Notification> findByCustomer(Long customerId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Agent> findByAgent(Long agentId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Notification> findByAdmin(Long adminId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Notification> findByDeveloper(Long developerId, Pageable pageable) {
        return null;
    }
}
