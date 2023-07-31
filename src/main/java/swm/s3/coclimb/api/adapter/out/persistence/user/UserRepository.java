package swm.s3.coclimb.api.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import swm.s3.coclimb.api.application.port.out.persistence.user.UserLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.user.UserUpdatePort;
import swm.s3.coclimb.api.exception.errortype.user.UserNotFound;
import swm.s3.coclimb.domain.user.User;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements UserLoadPort, UserUpdatePort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByInstagramUserId(Long instagramUserId) {
        return userJpaRepository.findByInstagramUserInfoId(instagramUserId);
    }

    @Override
    public Long save(User user) {
        Long id = userJpaRepository.save(user).getId();
        return id;
    }

    @Override
    public User getById(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(UserNotFound::new);
    }
}
