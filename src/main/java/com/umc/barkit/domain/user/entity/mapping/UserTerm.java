package com.umc.barkit.domain.user.entity.mapping;

import com.umc.barkit.domain.user.entity.Term;
import com.umc.barkit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "user_terms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_terms_user_term", columnNames = {"user_id", "term_id"})
        })
public class UserTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(name = "is_agreed", nullable = false)
    private Boolean isAgreed;

    @Column(name = "agreed_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime agreedAt;

    public static UserTerm agree(User user, Term term, LocalDateTime agreedAt) {
        return UserTerm.builder()
                .user(user)
                .term(term)
                .isAgreed(true)
                .agreedAt(agreedAt)
                .build();
    }

    public void revoke() {
        this.isAgreed = false;
        this.agreedAt = null;
    }
}
