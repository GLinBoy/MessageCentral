package com.glinboy.app.repository;

import com.glinboy.app.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Token entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TokenRepository extends JpaRepository<Token, Long>, JpaSpecificationExecutor<Token> {
    @Modifying
    @Query("update Token t set t.disable = :disable where t.id = :id")
    void updateTokenStatus(@Param("id") Long id, @Param("disable") Boolean disable);
}
