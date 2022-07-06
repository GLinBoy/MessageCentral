package com.glinboy.app.repository;

import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.domain.enumeration.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ShortMessage entity.
 */
@Repository
public interface ShortMessageRepository extends JpaRepository<ShortMessage, Long>, JpaSpecificationExecutor<ShortMessage> {

    @Modifying
    @Query("update ShortMessage s set s.status = :status where s.id in :ids")
    void updateStatus(@Param(value = "status") MessageStatus status, @Param(value = "ids") Long... ids);

}
