package com.glinboy.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.glinboy.app.domain.Notification;
import com.glinboy.app.domain.enumeration.MessageStatus;

/**
 * Spring Data SQL repository for the Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    @Modifying
    @Query("update Notification n set n.status = :status where n.id = :id")
    void updateStatus(@Param(value = "id") long id, @Param(value = "status") MessageStatus status);

}
