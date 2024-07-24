package com.sparta.eduwithme.domain.profile;

import com.sparta.eduwithme.common.exception.CustomException;
import com.sparta.eduwithme.common.exception.ErrorCode;
import com.sparta.eduwithme.domain.profile.dto.UpdatePasswordRequestDto;
import com.sparta.eduwithme.domain.profile.dto.UserProfileDto;
import com.sparta.eduwithme.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public UserProfileDto getUserProfile(Long userId) {
        User user = profileRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserProfileDto.builder()
                .email(user.getEmail())
                .nickName(user.getNickName())
                .photoUrl(user.getPhotoUrl())
                .ranking(user.getRanking())
                .build();
    }

    public void updateUserProfile(Long userId, String email, String newNickname) {
        User user = profileRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이메일이 일치하는지 확인
        if (!user.getEmail().equals(email)) {
            throw new CustomException(ErrorCode.EMAIL_MISMATCH);
        }

        user.updateNickname(newNickname);
        profileRepository.save(user);
    }

    public void updateUserPassword(Long userId, UpdatePasswordRequestDto request) {
        User user = profileRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 형식 검증
        if (!PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        profileRepository.save(user);
    }
}
