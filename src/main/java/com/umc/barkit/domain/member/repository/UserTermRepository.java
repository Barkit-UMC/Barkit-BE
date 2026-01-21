package com.umc.barkit.domain.member.repository;

import com.umc.barkit.domain.member.entity.mapping.UserTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTermRepository extends JpaRepository<UserTerm, Long> {
}
