package com.umc.barkit.domain.user.service.command;

import com.umc.barkit.domain.user.converter.UserConverter;
import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.entity.Term;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.entity.mapping.UserTerm;
import com.umc.barkit.domain.user.repository.TermRepository;
import com.umc.barkit.domain.user.repository.UserRepository;
import com.umc.barkit.domain.user.repository.UserTermRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final TermRepository termRepository;
    private final UserRepository userRepository;
    private final UserTermRepository userTermRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원가입
    public UserResponseDto.SignupResponseDto Signup(UserRequestDto.SignupRequestDto signupRequestDto){

        // 이메일 중복 확인
        if (userRepository.existsByEmail(signupRequestDto.email())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다");
        }

        // 비밀번호 확인
        if(!signupRequestDto.password().equals(signupRequestDto.confirmPassword())){
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDto.password());

        // User 생성
        User user = UserConverter.toUser(signupRequestDto, encodedPassword);

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
}
