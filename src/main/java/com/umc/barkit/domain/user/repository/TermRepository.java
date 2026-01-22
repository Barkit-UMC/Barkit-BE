package com.umc.barkit.domain.user.repository;

import com.umc.barkit.domain.user.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
}
