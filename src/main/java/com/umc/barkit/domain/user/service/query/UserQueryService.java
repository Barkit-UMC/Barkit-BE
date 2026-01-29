package com.umc.barkit.domain.user.service.query;

import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto.EmailCheckResponseDto;
import com.umc.barkit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    // 아이디 중복 확인
    public UserResponseDto.EmailCheckResponseDto checkEmailAvailability(String email) {
        boolean isAvailable = !userRepository.existsByEmail(email); // DB에서 해당 이메일이 존재하면 false 반환
        String message = isAvailable ? "사용 가능한 아이디입니다" : "사용 불가능한 아이디입니다";
        return new EmailCheckResponseDto(isAvailable, message);
    }

}
