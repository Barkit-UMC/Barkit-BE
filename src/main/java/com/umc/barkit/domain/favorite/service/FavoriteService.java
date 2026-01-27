package com.umc.barkit.domain.favorite.service;

import com.umc.barkit.domain.favorite.entity.FavoriteStoreBrand;
import com.umc.barkit.domain.favorite.enums.FavoriteStoreStatus;
import com.umc.barkit.domain.favorite.exception.code.FavoriteErrorCode;
import com.umc.barkit.domain.favorite.repository.FavoriteStoreBrandRepository;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.repository.UserRepository;
import com.umc.barkit.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteStoreBrandRepository favoriteRepository;
    private final UserRepository userRepository;

    /* 즐겨찾기 목록 조회 (최대 5개) */
    public List<FavoriteStoreBrand> getFavorites(Long userId) {
        return favoriteRepository.findTop5ByUser_IdAndStatusOrderByCreatedAtDesc(
                userId,
                FavoriteStoreStatus.ACTIVE
        );
    }

    /* 즐겨찾기 추가 */
    @Transactional
    public void createFavorite(Long userId, Long storeBrandId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(FavoriteErrorCode.FAVORITE_FORBIDDEN));

        // 최대 5개 제한
        long count = favoriteRepository.countByUser_IdAndStatus(
                userId,
                FavoriteStoreStatus.ACTIVE
        );
        if (count >= 5) {
            throw new GeneralException(FavoriteErrorCode.FAVORITE_LIMIT_EXCEEDED);
        }

        // 중복 방지
        boolean exists = favoriteRepository.existsByUser_IdAndStoreBrandIdAndStatus(
                userId,
                storeBrandId,
                FavoriteStoreStatus.ACTIVE
        );
        if (exists) {
            throw new GeneralException(FavoriteErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        FavoriteStoreBrand favorite = FavoriteStoreBrand.create(user, storeBrandId);
        favoriteRepository.save(favorite);
    }

    /* 즐겨찾기 삭제 (Soft Delete) */
    @Transactional
    public void deleteFavorite(Long userId, Long favoriteId) {

        FavoriteStoreBrand favorite = favoriteRepository.findByIdAndUser_Id(
                        favoriteId,
                        userId
                )
                .orElseThrow(() -> new GeneralException(FavoriteErrorCode.FAVORITE_NOT_FOUND));

        favorite.delete();
    }
}
