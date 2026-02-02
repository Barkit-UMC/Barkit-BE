package com.umc.barkit.domain.user.service.command;

import com.umc.barkit.domain.user.converter.UserConverter;
import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.entity.Term;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.entity.mapping.UserTerm;
import com.umc.barkit.domain.user.enums.Role;
import com.umc.barkit.domain.user.exception.UserException;
import com.umc.barkit.domain.user.exception.code.UserErrorCode;
import com.umc.barkit.domain.user.repository.TermRepository;
import com.umc.barkit.domain.user.repository.UserRepository;
import com.umc.barkit.domain.user.repository.UserTermRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final TermRepository termRepository;
    private final UserRepository userRepository;
    private final UserTermRepository userTermRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public UserResponseDto.SignupResponseDto signup(UserRequestDto.SignupRequestDto signupRequestDto){

        // 이메일 중복 확인
        if (userRepository.existsByEmail(signupRequestDto.email())) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 확인
        if(!signupRequestDto.password().equals(signupRequestDto.confirmPassword())){
            throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDto.password());

        // User 생성
        User user = UserConverter.toUser(signupRequestDto, encodedPassword, Role.ROLE_USER);

        // User 저장
        userRepository.save(user);

        // Term 리스트 조회
        List<Term> terms = termRepository.findAll();

        // UserTerm 생성
        List<UserTerm> userTerms = UserConverter.toUserTermEntities(signupRequestDto.terms(), user, terms);
        userTermRepository.saveAll(userTerms);

        // DTO 응답
        return new UserResponseDto.SignupResponseDto(user.getId(), user.getEmail());
    }

    // 생년월일 변경
    @Transactional
    public void updateBirthDate(Long userId, LocalDate birthDate){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        user.updateBirthDate(birthDate);
    }
}
