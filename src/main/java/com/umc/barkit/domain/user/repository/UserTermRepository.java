package com.umc.barkit.domain.user.repository;

import com.umc.barkit.domain.user.entity.mapping.UserTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTermRepository extends JpaRepository<UserTerm, Long> {
}
