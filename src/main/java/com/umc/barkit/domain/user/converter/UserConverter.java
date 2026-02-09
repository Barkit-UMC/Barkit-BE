package com.umc.barkit.domain.user.converter;

import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.entity.Term;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.entity.mapping.UserTerm;
import com.umc.barkit.domain.user.enums.Role;
import com.umc.barkit.domain.user.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.List;

public class UserConverter {

    // SignupRequestDto -> User Entity
    public static User toUser(
            UserRequestDto.SignupRequestDto dto,
            String passwordHash,
            Role role
    ){
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .passwordHash(passwordHash)
                .role(role)
                .phoneNumber(null)
                .birthDate(dto.birthDate())
                .status(UserStatus.ACTIVE) // 초기 상태는 ACTIVE
                .deletedAt(null) // 삭제 시간은 null
                .build();
    }

    // TermAgreement DTO -> UserTerm Entity
    public static List<UserTerm> toUserTermEntities(List<UserRequestDto.TermAgreement> termAgreements, User user,
                                                    List<Term> terms) {
        LocalDateTime agreedAt = LocalDateTime.now(); // 현재 시간

        return termAgreements.stream()
                .map(agreement -> {
                    Term term = terms.stream()
                            .filter(t -> t.getId().equals(agreement.termId())) // TermId로 Term 찾기
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Invalid term ID"));

                    return UserTerm.agree(user, term, agreedAt); // UserTerm 엔티티 생성
                })
                .toList();
    }

    // User -> LoginResponseDto
    public static UserResponseDto.LoginResponseDto toLoginDTO(
            User user,
            String accessToken,
            String refreshToken
    ){
        return UserResponseDto.LoginResponseDto.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // User Entity -> PersonalInfoResponseDto
    public static UserResponseDto.PersonalInfoResponseDto toPersonalInfoDto(User user) {
        return new UserResponseDto.PersonalInfoResponseDto(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getBirthDate()
        );
    }

    public static User toSocialUser(
            String email,
            String displayName,
            String passwordHash,
            Role role
    ) {
        return User.builder()
                .name(displayName)
                .email(email)
                .passwordHash(passwordHash)
                .role(role)
                .phoneNumber(null)
                .birthDate(null)
                .status(UserStatus.ACTIVE)
                .deletedAt(null)
                .build();
    }
}
