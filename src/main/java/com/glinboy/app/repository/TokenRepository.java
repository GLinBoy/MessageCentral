package com.glinboy.app.repository;

import com.glinboy.app.domain.Token;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Token entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TokenRepository extends JpaRepository<Token, Long>, JpaSpecificationExecutor<Token> {}
