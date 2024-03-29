package com.glinboy.app.repository;

import com.glinboy.app.domain.Email;
import com.glinboy.app.domain.enumeration.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Email entity.
 */
@Repository
public interface EmailRepository extends JpaRepository<Email, Long>, JpaSpecificationExecutor<Email> {

    @Modifying
    @Query("update Email e set e.status = :status where e.id in :ids")
    void updateStatus(@Param(value = "status") MessageStatus status, @Param(value = "ids") Long... ids);

}
