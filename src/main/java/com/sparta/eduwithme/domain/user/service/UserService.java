package com.sparta.eduwithme.domain.user.service;

import com.sparta.eduwithme.common.exception.CustomException;
import com.sparta.eduwithme.common.exception.ErrorCode;
import com.sparta.eduwithme.domain.room.entity.Room;
import com.sparta.eduwithme.domain.room.repository.RoomRepository;
import com.sparta.eduwithme.domain.room.repository.StudentRepository;
import com.sparta.eduwithme.domain.user.UserRepository;
import com.sparta.eduwithme.domain.user.dto.SignupRequestDto;
import com.sparta.eduwithme.domain.user.entity.User;
import com.sparta.eduwithme.util.JwtUtil;
import com.sparta.eduwithme.util.RedisUtil;
import java.util.UUID;
import com.vane.badwordfiltering.BadWordFiltering;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final MailSendService mailSendService;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;

    private final BadWordFiltering badWordFiltering = new BadWordFiltering();

    // 회원가입 이메일 인증 코드 발송 메서드
    public String sendSignupVerificationEmail(String email) {
        // 이미 존재하는 이메일인지 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }
        return mailSendService.joinEmail(email);
    }

    // 이메일 인증 코드 확인 메서드
    public void verifySignupEmail(String email, String authCode) {
        // 인증 코드 일치 여부 확인
        if (!mailSendService.CheckAuthNum(email, authCode)) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        // 인증 완료 상태를 Redis에 5분간 저장
        redisUtil.setDataExpire(email, "VERIFIED", 60 * 5L);
    }

    public void accessTokenReissue(String refreshToken, HttpServletResponse res) {
        Claims info = jwtUtil.getUserInfoFromToken(jwtUtil.refreshTokenSubstring(refreshToken));

        User user = userRepository.findByEmail(info.getSubject()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        String newAccessToken = jwtUtil.createAccessToken(user);
        String newRefreshToken = jwtUtil.createRefreshToken(user);
        res.addHeader(JwtUtil.ACCESS_TOKEN_HEADER, newAccessToken);
        res.addHeader(JwtUtil.REFRESH_TOKEN_HEADER, newRefreshToken);
        String newRefreshTokenOriginal = jwtUtil.refreshTokenSubstring(newRefreshToken);
        user.updateRefreshToken(newRefreshTokenOriginal);

    }

    // 회원가입 처리 메서드
    @Transactional
    public void signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickName = requestDto.getNickName();

        if (badWordFiltering.check(requestDto.getNickName())) {
            throw new CustomException(ErrorCode.PROFANITY_DETECTED);
        }

        // 이메일 중복 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }

        // 이메일 인증 완료 여부 확인
        if (!"VERIFIED".equals(redisUtil.getData(email))) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        userRepository.save(new User(email, password, nickName));
        redisUtil.deleteData(email);
    }

    // 주어진 이메일이 등록된 이메일인지 확인하는 메서드
    public boolean isRegisteredEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // 임시 비밀번호 발급을 요청하는 메서드
    public void requestTempPassword(String email) {
        // 이메일이 등록되지 않았다면 예외를 던집니다.
        if (!isRegisteredEmail(email)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        // 임시 비밀번호 발급을 위한 이메일을 보냅니다.
        mailSendService.sendTempPasswordEmail(email);
    }

    public void resetPasswordWithTempPassword(String email, String authCode) {
        if (!mailSendService.CheckAuthNum(email, authCode)) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String tempPassword = generateTempPassword();
        user.updatePassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        mailSendService.sendTempPassword(email, tempPassword);

        redisUtil.deleteData(authCode);
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.findByNickName(nickname).isPresent();
    }

    // 회원탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자가 생성한 방 삭제
        List<Room> userRooms = roomRepository.findAllByManagerUserId(userId);
        roomRepository.deleteAll(userRooms);

        studentRepository.deleteAllByUserId(userId);

        // 사용자가 참여한 방에서 학생 정보 삭제
        user.getStudents().clear();

        // 사용자의 학습 상태 삭제
//        learningStatusRepository.deleteAllByUserId(userId);

        // 사용자가 작성한 댓글 삭제
//        commentRepository.deleteAllByUserId(userId);

        // 마지막으로 사용자 삭제
        userRepository.delete(user);
    }
}

