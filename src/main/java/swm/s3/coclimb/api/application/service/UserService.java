package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.application.port.in.user.UserCommand;
import swm.s3.coclimb.api.application.port.in.user.UserQuery;
import swm.s3.coclimb.api.application.port.out.persistence.user.UserLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.user.UserUpdatePort;
import swm.s3.coclimb.domain.user.InstagramUserInfo;
import swm.s3.coclimb.domain.user.User;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserQuery, UserCommand {

    private final UserLoadPort userLoadPort;
    private final UserUpdatePort userUpdatePort;


    @Override
    public Optional<User> findUserByInstagramUserId(Long instagramUserId) {
        return userLoadPort.findByInstagramUserId(instagramUserId);
    }

    @Override
    @Transactional
    public Long createUserByInstagramInfo(InstagramUserInfo instagramUserInfo) {
        return userUpdatePort.save((User.builder()
                .name(UUID.randomUUID().toString().substring(8))
                .instagramUserInfo(InstagramUserInfo.builder()
                        .id(instagramUserInfo.getId())
                        .accessToken(instagramUserInfo.getAccessToken())
                        .tokenExpireTime(instagramUserInfo.getTokenExpireTime())
                        .build())
                .build()));
    }
}
