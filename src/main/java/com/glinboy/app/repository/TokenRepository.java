package com.glinboy.app.repository;

import com.glinboy.app.domain.Token;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Token entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TokenRepository extends JpaRepository<Token, Long>, JpaSpecificationExecutor<Token> {
    @Modifying
    @Query("update token t set t.disable = :disable where t.id = :id")
    void updateTokenStatus(@Param("id") Long id, @Param("disable") Boolean disable);
}
