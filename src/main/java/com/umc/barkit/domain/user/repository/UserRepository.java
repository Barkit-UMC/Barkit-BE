package com.umc.barkit.domain.user.repository;

import com.umc.barkit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);  // 아이디(이메일) 중복 확인
}
