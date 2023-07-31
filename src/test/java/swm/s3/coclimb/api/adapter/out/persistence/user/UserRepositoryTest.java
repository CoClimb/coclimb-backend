package swm.s3.coclimb.api.adapter.out.persistence.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.exception.errortype.user.UserNotFound;
import swm.s3.coclimb.domain.user.User;

class UserRepositoryTest extends IntegrationTestSupport {


    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }
    @Test
    @DisplayName("id로 사용자 정보를 조회한다.")
    void getById() throws Exception {
        // given
        Long userId = userJpaRepository.save(User.builder()
                        .name("username")
                        .build()).getId();

        // when
        User sut = userRepository.getById(userId);

        // then
        Assertions.assertThat(sut)
                .hasFieldOrPropertyWithValue("name", "username");
    }

    @Test
    @DisplayName("id로 사용자 정보 조회 시, 결과가 존재하지 않으면 에러가 발생한다.")
    void getUserNotExistsById() throws Exception {
        // given
        Long invalidUserId = 1L;
        // when, then
        Assertions.assertThatThrownBy(() -> userRepository.getById(invalidUserId))
                .isInstanceOf(UserNotFound.class);
    }
}