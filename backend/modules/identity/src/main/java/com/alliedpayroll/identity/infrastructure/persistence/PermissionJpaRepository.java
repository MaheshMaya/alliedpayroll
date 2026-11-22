package com.alliedpayroll.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface PermissionJpaRepository extends JpaRepository<PermissionEntity, String> {}
