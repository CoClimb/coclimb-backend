package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.adapter.out.instagram.dto.ShortLivedTokenResponse;
import swm.s3.coclimb.api.application.port.in.login.LoginCommand;
import swm.s3.coclimb.api.application.port.in.user.UserCommand;
import swm.s3.coclimb.api.application.port.in.user.UserQuery;
import swm.s3.coclimb.api.application.port.out.instagram.InstagramAuthPort;
import swm.s3.coclimb.domain.user.InstagramUserInfo;
import swm.s3.coclimb.domain.user.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService implements LoginCommand {
    private final InstagramAuthPort instagramAuthPort;
    private final UserCommand userCommand;
    private final UserQuery userQuery;

    @Override
    @Transactional
    public Long loginWithInstagram(String code) {
        ShortLivedTokenResponse shortLivedTokenResponse = instagramAuthPort.getShortLivedTokenAndUserId(code);
        User user = userQuery.findUserByInstagramUserId((shortLivedTokenResponse.getUserId()))
                .orElse(null);

        if (user == null) {
            InstagramUserInfo instagram = instagramAuthPort.getNewInstagramInfo(shortLivedTokenResponse);
            return userCommand.createUserByInstagramInfo(instagram);
        } else {
            instagramAuthPort.updateInstagramToken(user.getInstagramUserInfo(), shortLivedTokenResponse);
            return user.getId();
        }
    }
}
