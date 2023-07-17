package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.application.port.in.user.UserCommand;
import swm.s3.coclimb.api.application.port.out.user.UserLoadPort;
import swm.s3.coclimb.api.application.port.out.user.UserUpdatePort;
import swm.s3.coclimb.api.domain.User;
import swm.s3.coclimb.api.oauth.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.oauth.instagram.dto.LongLivedTokenResponseDto;
import swm.s3.coclimb.api.oauth.instagram.dto.ShortLivedTokenResponseDto;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserCommand {

    private final UserLoadPort userLoadPort;
    private final UserUpdatePort userUpdatePort;

    private final InstagramRestApiManager instagramRestApiManager;

    @Override
    @Transactional
    public void loginInstagram(String code) {
        if(code == null) {
            throw new RuntimeException("유효하지 않은 인증 코드");
        }

        ShortLivedTokenResponseDto shortLivedTokenResponseDto = instagramRestApiManager.getShortLivedAccessTokenAndUserId(code);
        User user = userLoadPort.findByInstaUserId(shortLivedTokenResponseDto.getUserId());
        LocalDate nowDate = LocalDate.now();

        if(user == null) {
            LongLivedTokenResponseDto longLivedTokenResponseDto = instagramRestApiManager.getLongLivedAccessToken(shortLivedTokenResponseDto.getShortLivedAccessToken());
            userUpdatePort.save(User.builder()
                    .instaUserId(shortLivedTokenResponseDto.getUserId())
                    .instaAccessToken(longLivedTokenResponseDto.getLongLivedAccessToken())
                    .instaTokenExpireDate(nowDate.plusDays(longLivedTokenResponseDto.getExpiresIn()/86400))
                    .build());
        } else {
            Period gap = Period.between(nowDate, user.getInstaTokenExpireDate());

            if(gap.getMonths() <= 0) {
                LongLivedTokenResponseDto longLivedTokenResponseDto;

                if(gap.getDays() > 0) {
                    longLivedTokenResponseDto = instagramRestApiManager.refreshLongLivedToken(user.getInstaAccessToken());
                } else {
                    longLivedTokenResponseDto = instagramRestApiManager.getLongLivedAccessToken(shortLivedTokenResponseDto.getShortLivedAccessToken());
                }

                user.update(User.builder()
                        .instaAccessToken(longLivedTokenResponseDto.getLongLivedAccessToken())
                        .instaTokenExpireDate(nowDate.plusDays(longLivedTokenResponseDto.getExpiresIn()/86400))
                        .build());
            }
        }
    }
}
