package com.glinboy.app.repository;

import com.glinboy.app.domain.Notification;
import com.glinboy.app.domain.enumeration.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    @Modifying
    @Query("update Notification n set n.status = :status where n.id in :ids")
    void updateStatus(@Param(value = "status") MessageStatus status, @Param(value = "ids") Long... ids);

}
