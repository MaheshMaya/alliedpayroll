package com.alliedpayroll.tenant.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Queries run for the bound tenant; Hibernate adds the tenant predicate. */
interface TerminologyOverrideJpaRepository extends JpaRepository<TerminologyOverrideEntity, UUID> {

  List<TerminologyOverrideEntity> findAllByLocale(String locale);

  Optional<TerminologyOverrideEntity> findByLocaleAndConceptKey(String locale, String conceptKey);
}
