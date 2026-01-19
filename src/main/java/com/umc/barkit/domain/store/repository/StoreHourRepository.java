package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.StoreBrand;
import com.umc.barkit.domain.store.entity.StoreHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreHourRepository extends JpaRepository<StoreHour,Long> {

}
