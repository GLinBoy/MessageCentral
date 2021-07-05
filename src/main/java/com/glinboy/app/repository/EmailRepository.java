package com.glinboy.app.repository;

import com.glinboy.app.domain.Email;
import com.glinboy.app.domain.enumeration.MessageStatus;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Email entity.
 */
@Repository
public interface EmailRepository extends JpaRepository<Email, Long>, JpaSpecificationExecutor<Email> {

    @Modifying
    @Query("update Email e set e.status = :status where e.id = :id")
    void updateStatus(@Param(value = "id") long id, @Param(value = "status") MessageStatus status);

}