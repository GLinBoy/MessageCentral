package com.glinboy.app.repository;

import com.glinboy.app.domain.ShortMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShortMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShortMessageRepository extends JpaRepository<ShortMessage, Long>, JpaSpecificationExecutor<ShortMessage> {}
