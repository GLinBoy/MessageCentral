package com.glinboy.app.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenPrincipalRepository extends TokenRepository {
    @Modifying
    @Query("update Token t set t.disable = :disable where t.id = :id")
    void updateTokenStatus(@Param("id") Long id, @Param("disable") Boolean disable);
}
