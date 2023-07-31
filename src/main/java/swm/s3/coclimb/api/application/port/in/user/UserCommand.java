package swm.s3.coclimb.api.application.port.in.user;

import swm.s3.coclimb.domain.user.InstagramUserInfo;

public interface UserCommand {
    Long createUserByInstagramInfo(InstagramUserInfo instagram);
}
