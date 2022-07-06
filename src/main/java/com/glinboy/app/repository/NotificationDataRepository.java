package com.glinboy.app.repository;

import com.glinboy.app.domain.NotificationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the NotificationData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationDataRepository extends JpaRepository<NotificationData, Long> {
}
