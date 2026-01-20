package com.umc.barkit.domain.uuid.repository;

import com.umc.barkit.domain.uuid.entity.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UuidRepository extends JpaRepository<Uuid, Long> {
}