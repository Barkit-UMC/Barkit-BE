package com.umc.barkit.domain.user.service.query;

import com.umc.barkit.domain.user.converter.UserConverter;
import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto.EmailCheckResponseDto;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.exception.UserException;
import com.umc.barkit.domain.user.exception.code.UserErrorCode;
import com.umc.barkit.domain.user.repository.UserRepository;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import com.umc.barkit.global.auth.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    // 아이디 중복 확인
    public UserResponseDto.EmailCheckResponseDto checkEmailAvailability(String email) {
        boolean isAvailable = !userRepository.existsByEmail(email); // DB에서 해당 이메일이 존재하면 false 반환
        String message = isAvailable ? "사용 가능한 아이디입니다" : "사용 불가능한 아이디입니다";
        return new EmailCheckResponseDto(isAvailable, message);
    }

    // 로그인
    public UserResponseDto.LoginResponseDto login(
            UserRequestDto.@Valid LoginRequestDto dto
    ) {

        // User 조회
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 비밀번호 검증
        if (!encoder.matches(dto.password(), user.getPasswordHash())){
            throw new UserException(UserErrorCode.INVALID);
        }

        // JWT 토큰 발급용 UserDetails
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // 엑세스 토큰 발급
        String accessToken = jwtUtil.createAccessToken(userDetails);

        // DTO 조립
        return UserConverter.toLoginDTO(user, accessToken);
    }

    // 개인정보 조회
    public UserResponseDto.PersonalInfoResponseDto getPersonalInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new UserException(UserErrorCode.NOT_FOUND);
        }

        return UserConverter.toPersonalInfoDto(user);
    }
}
