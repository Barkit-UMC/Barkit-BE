package com.umc.barkit.domain.user.repository;

import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.enums.UserStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAndStatus(String email, UserStatus status); // 아이디(이메일) 중복 확인
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    Optional<User> findByIdAndStatus(Long id, UserStatus status);
}
