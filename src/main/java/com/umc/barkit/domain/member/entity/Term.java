package com.umc.barkit.domain.member.entity;

import com.umc.barkit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "terms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_terms_code_version", columnNames = {"code", "version"})
        })
public class Term extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 30, nullable = false)
    private String code;

    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;
}
