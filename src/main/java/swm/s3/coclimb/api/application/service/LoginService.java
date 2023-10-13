package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.dto.ShortLivedTokenResponse;
import swm.s3.coclimb.api.adapter.out.oauth.kakao.dto.KakaoTokenResponse;
import swm.s3.coclimb.api.application.port.in.login.LoginCommand;
import swm.s3.coclimb.api.application.port.in.user.UserCommand;
import swm.s3.coclimb.api.application.port.in.user.UserQuery;
import swm.s3.coclimb.api.application.port.out.oauth.instagram.InstagramAuthPort;
import swm.s3.coclimb.api.application.port.out.oauth.kakao.KakaoAuthPort;
import swm.s3.coclimb.api.application.port.out.oauth.kakao.KakaoDataPort;
import swm.s3.coclimb.config.ServerClock;
import swm.s3.coclimb.domain.user.InstagramUserInfo;
import swm.s3.coclimb.domain.user.KakaoUserInfo;
import swm.s3.coclimb.domain.user.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService implements LoginCommand {
    private final InstagramAuthPort instagramAuthPort;
    private final KakaoAuthPort kakaoAuthPort;
    private final KakaoDataPort kakaoDataPort;
    private final UserCommand userCommand;
    private final UserQuery userQuery;
    private final ServerClock serverClock;

    @Override
    @Transactional
    public Long loginWithInstagram(String code) {
        ShortLivedTokenResponse shortLivedTokenResponse = instagramAuthPort.getShortLivedTokenAndUserId(code);
        User user = userQuery.findUserByInstagramUserId((shortLivedTokenResponse.getUserId()))
                .orElse(null);

        if (user == null) {
            InstagramUserInfo instagramUserInfo = instagramAuthPort.getNewInstagramUserInfo(shortLivedTokenResponse);
            return userCommand.createUserByInstagramInfo(instagramUserInfo);
        } else {
            instagramAuthPort.updateInstagramToken(user.getInstagramUserInfo(), shortLivedTokenResponse);
            return user.getId();
        }
    }

    @Override
    @Transactional
    public Long loginWithKakao(String code) {
        KakaoTokenResponse kakaoTokenResponse = kakaoAuthPort.getToken(code);
        Long kakaoUserId = kakaoDataPort.getKakaoUserId(kakaoTokenResponse.getAccessToken());
        User user = userQuery.findUserByKakaoUserId(kakaoUserId)
                .orElse(null);

        if(user == null) {
            KakaoUserInfo kakaoUserInfo = kakaoTokenResponse.toKakaoUserInfoEntity(serverClock.getDateTime(), kakaoUserId);
            return userCommand.createUserByKakaoInfo(kakaoUserInfo);
        } else {
            return user.getId();
        }
    }
}
