package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.application.port.in.UserCommand;
import swm.s3.coclimb.api.application.port.out.UserLoadPort;
import swm.s3.coclimb.api.application.port.out.UserUpdatePort;
import swm.s3.coclimb.api.domain.User;
import swm.s3.coclimb.api.oauth.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.oauth.instagram.dto.LongLivedTokenResponse;
import swm.s3.coclimb.api.oauth.instagram.dto.ShortLivedTokenResponse;

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

        ShortLivedTokenResponse shortLivedTokenResponse = instagramRestApiManager.getShortLivedAccessTokenAndUserId(code);
        User user = userLoadPort.findByInstaUserId(shortLivedTokenResponse.getUserId());
        LocalDate nowDate = LocalDate.now();

        if(user == null) {
            LongLivedTokenResponse longLivedTokenResponse = instagramRestApiManager.getLongLivedAccessToken(shortLivedTokenResponse.getShortLivedAccessToken());
            userUpdatePort.save(User.builder()
                    .instaUserId(shortLivedTokenResponse.getUserId())
                    .instaAccessToken(longLivedTokenResponse.getLongLivedAccessToken())
                    .instaTokenExpireDate(nowDate.plusDays(longLivedTokenResponse.getExpiresIn()/86400))
                    .build());
        } else {
            Period gap = Period.between(nowDate, user.getInstaTokenExpireDate());

            if(gap.getMonths() <= 0) {
                LongLivedTokenResponse longLivedTokenResponse;

                if(gap.getDays() > 0) {
                    longLivedTokenResponse = instagramRestApiManager.refreshLongLivedToken(user.getInstaAccessToken());
                } else {
                    longLivedTokenResponse = instagramRestApiManager.getLongLivedAccessToken(shortLivedTokenResponse.getShortLivedAccessToken());
                }

                user.update(User.builder()
                        .instaAccessToken(longLivedTokenResponse.getLongLivedAccessToken())
                        .instaTokenExpireDate(nowDate.plusDays(longLivedTokenResponse.getExpiresIn()/86400))
                        .build());
            }
        }
    }
}
