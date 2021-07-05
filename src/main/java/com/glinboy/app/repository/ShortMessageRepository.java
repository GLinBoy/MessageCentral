package com.glinboy.app.repository;

import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.domain.enumeration.MessageStatus;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ShortMessage entity.
 */
@Repository
public interface ShortMessageRepository extends JpaRepository<ShortMessage, Long>, JpaSpecificationExecutor<ShortMessage> {

    @Modifying
    @Query("update ShortMessage s set s.status = :status where s.id = :id")
    void updateStatus(@Param(value = "id") long id, @Param(value = "status") MessageStatus status);

}
