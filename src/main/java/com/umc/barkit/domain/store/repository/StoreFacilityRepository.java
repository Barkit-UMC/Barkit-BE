package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.Facility;
import com.umc.barkit.domain.store.entity.mapping.StoreFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreFacilityRepository extends JpaRepository<StoreFacility,Long> {

}
