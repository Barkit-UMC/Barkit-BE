package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.Facility;
import com.umc.barkit.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility,Long> {

}
