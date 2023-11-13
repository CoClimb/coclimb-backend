package swm.s3.coclimb.api.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.filestore.AwsS3Manager;
import swm.s3.coclimb.domain.gymlike.GymLike;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.user.InstagramUserInfo;
import swm.s3.coclimb.domain.user.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

class UserServiceTest extends IntegrationTestSupport {

    @MockBean
    AwsS3Manager awsS3Manager;

    @Test
    @DisplayName("InstagramUserId 로 사용자를 조회한다.")
    void findUserByInstagramUserId() throws Exception {
        // given
        userJpaRepository.save(User.builder()
                .name("user")
                .instagramUserInfo(InstagramUserInfo.builder()
                        .id(1L)
                        .build())
                .build());
        // when
        Optional<User> sut = userService.findUserByInstagramUserId(1L);

        // then
        assertThat(sut).isNotNull();
        assertThat(sut.get())
                .extracting("name", "instagramUserInfo.id")
                .containsExactly("user", 1L);
    }

    @Test
    @DisplayName("인스타그램의 정보를 사용하여 사용자를 생성한다.")
    void createUserByInstagramInfo() throws Exception {
        // given
        InstagramUserInfo instagramUserInfo = InstagramUserInfo.builder()
                .name("user")
                .id(1L)
                .build();

        // when
        Long userId = userService.createUserByInstagramInfo(instagramUserInfo);
        User sut = userJpaRepository.findById(userId).orElse(null);

        // then
        assertThat(userId).isNotNull();
        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isNotEmpty();
        assertThat(sut.getInstagramUserInfo())
                .hasFieldOrPropertyWithValue("id",1L);
    }

    @Test
    @DisplayName("유저 삭제 시 관련 데이터와 함께 삭제된다.")
    void deleteUser() {
        //given
        willDoNothing().given(awsS3Manager).deleteFile(any());
        userJpaRepository.save(User.builder().build());
        User user = userJpaRepository.findAll().get(0);
        IntStream.range(0,5).forEach(i -> {
            mediaRepository.save(Media.builder()
                    .user(user)
                    .build());
            gymLikeRepository.save(GymLike.builder()
                    .user(user)
                    .build());
        });

        //when
        userService.deleteUser(user);
        User sut1 = userJpaRepository.findById(user.getId()).orElse(null);
        List<Media> sut2 = mediaJpaRepository.findAll();
        List<GymLike> sut3 = gymLikeJpaRepository.findAll();

        //then
        then(awsS3Manager).should(times(10)).deleteFile(any());
        assertThat(sut1).isNull();
        assertThat(sut2).isEmpty();
        assertThat(sut3).isEmpty();
    }
}