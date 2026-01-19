package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.StorePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorePhotoRepository extends JpaRepository<StorePhoto,Long> {

}
