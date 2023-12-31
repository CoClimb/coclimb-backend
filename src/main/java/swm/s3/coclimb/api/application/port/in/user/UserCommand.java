package swm.s3.coclimb.api.application.port.in.user;

import swm.s3.coclimb.api.application.port.in.user.dto.UserUpdateRequestDto;
import swm.s3.coclimb.domain.user.InstagramUserInfo;
import swm.s3.coclimb.domain.user.KakaoUserInfo;
import swm.s3.coclimb.domain.user.User;

public interface UserCommand {
    Long createUserByInstagramInfo(InstagramUserInfo instagram);

    Long createUserByKakaoInfo(KakaoUserInfo kakaoUserInfo);

    void updateUser(UserUpdateRequestDto userUpdateRequestDto);

    void deleteUser(User user);
}
