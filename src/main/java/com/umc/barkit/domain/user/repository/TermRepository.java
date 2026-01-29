package com.umc.barkit.domain.user.repository;

import com.umc.barkit.domain.user.entity.Term;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

    // 활성화된 약관만 가져오되, 같은 약관이 여러 버전이면 최신 버전 가져오기
    @Query("""
        select t from Term t
        where t.isActive = true
          and t.version = (
              select max(t2.version) from Term t2
              where t2.code = t.code and t2.isActive = true
          )
        order by t.isRequired desc, t.id asc
    """)
    List<Term> findActiveLatestTerms();
}
