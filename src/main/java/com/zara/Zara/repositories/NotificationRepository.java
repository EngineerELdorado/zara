package com.zara.Zara.repositories;

import com.zara.Zara.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = "select * from notifications where business_id=?1",
    countQuery = "select count(*) from notifications where business_id=?1",
    nativeQuery = true)
    Page<Notification> findByBusiness(Long businessId, Pageable pageable);


    @Query(value = "select * from notifications where agent_id=?1",
            countQuery = "select count(*) from notifications where agent_id=?1",
            nativeQuery = true)
    Page<Notification> findByAgent(Long agentId, Pageable pageable);

    @Query(value = "select * from notifications where developer_id=?1",
            countQuery = "select count(*) from notifications where developer_id=?1",
            nativeQuery = true)
    Page<Notification> findByDeveloper(Long developerId, Pageable pageable);

    @Query(value = "select * from notifications where customer_id=?1",
            countQuery = "select count(*) from notifications where customer_id=?1",
            nativeQuery = true)
    Page<Notification> findByCustomer(Long customerId, Pageable pageable);

}
