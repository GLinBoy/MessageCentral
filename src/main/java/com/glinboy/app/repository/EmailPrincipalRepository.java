package com.glinboy.app.repository;

import com.glinboy.app.domain.enumeration.MessageStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailPrincipalRepository extends EmailRepository {
    @Modifying
    @Query("update Email e set e.status = :status where e.id in :ids")
    void updateStatus(@Param(value = "status") MessageStatus status, @Param(value = "ids") Long... ids);
}
