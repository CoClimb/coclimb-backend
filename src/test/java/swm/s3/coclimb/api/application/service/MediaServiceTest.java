package swm.s3.coclimb.api.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaPageRequestDto;
import swm.s3.coclimb.api.exception.errortype.media.InstagramMediaIdConflict;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;
import swm.s3.coclimb.domain.user.User;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MediaServiceTest extends IntegrationTestSupport {

    @MockBean
    private InstagramRestApiManager instagramRestApiManager;

    @Test
    @DisplayName("인스타그램 미디어 타입 중 VIDEO만 필터링하여 반환한다.")
    void getMyInstagramVideos() {
        //given
        given(instagramRestApiManager.getMyMedias(any(String.class))).willReturn(List.of(
                new TestInstagramMediaResponseDto("1", "VIDEO"),
                new TestInstagramMediaResponseDto("2", "IMAGE")
        ));
        //when
        List<InstagramMediaResponseDto> sut = mediaService.getMyInstagramVideos("accessToken");

        //then
        assertThat(sut).hasSize(1)
                .extracting("mediaId", "mediaType")
                .containsExactly(tuple("1", "VIDEO"));
    }

    @Test
    @DisplayName("미디어를 저장할 수 있다.")
    void save() {
        //given
        userJpaRepository.save(User.builder().build());
        Long userId = userJpaRepository.findAll().get(0).getId();
        User user = userJpaRepository.findById(userId).get();

        MediaCreateRequestDto mediaCreateRequestDto = MediaCreateRequestDto.builder()
                .user(user)
                .instagramMediaInfo(InstagramMediaInfo.builder()
                        .permalink("instagramPermalink")
                        .build())
                .mediaProblemInfo(MediaProblemInfo.builder()
                        .color("problemColor")
                        .build())
                .build();

        //when
        mediaService.createMedia(mediaCreateRequestDto);
        Media sut = mediaJpaRepository.findByUserId(userId).get(0);

        //then
        assertThat(sut.getUser().getId()).isEqualTo(userId);
        assertThat(sut.getInstagramMediaInfo().getPermalink()).isEqualTo("instagramPermalink");
        assertThat(sut.getMediaProblemInfo().getColor()).isEqualTo("problemColor");
    }

    @Test
    @DisplayName("인스타그램 미디어 ID가 중복되면 예외가 발생한다.")
    void saveDuplicateInstagramMediaId() {
        //given
        String instagramMediaId = "instagramMediaId";

        MediaCreateRequestDto mediaCreateRequestDto = MediaCreateRequestDto.builder()
                .instagramMediaInfo(InstagramMediaInfo.builder()
                        .id(instagramMediaId)
                        .build())
                .build();

        mediaJpaRepository.save(Media.builder()
                .instagramMediaInfo(InstagramMediaInfo.builder()
                        .id(instagramMediaId)
                        .build())
                .build());
        //when
        //then
        assertThatThrownBy(() -> mediaService.createMedia(mediaCreateRequestDto))
                .isInstanceOf(InstagramMediaIdConflict.class);
    }

    @Test
    @DisplayName("전체 미디어를 페이징 조회할 수 있다.")
    void getPagedMedias() {
        //given
        mediaJpaRepository.saveAll(IntStream.range(0, 10)
                .mapToObj(i -> Media.builder()
                        .instagramMediaInfo(InstagramMediaInfo.builder()
                                .id(String.valueOf(i))
                                .build())
                        .build())
                .toList());


        MediaPageRequestDto mediaPageRequestDto0 = MediaPageRequestDto.builder()
                .page(0)
                .size(5)
                .build();
        MediaPageRequestDto mediaPageRequestDto1 = MediaPageRequestDto.builder()
                .page(1)
                .size(5)
                .build();

        //when
        Page<Media> sut0 = mediaService.getPagedMedias(mediaPageRequestDto0);
        Page<Media> sut1 = mediaService.getPagedMedias(mediaPageRequestDto1);

        //then
        assertThat(sut0.getTotalElements()).isEqualTo(10);
        assertThat(sut0.getTotalPages()).isEqualTo(2);
        assertThat(sut0.getContent()).hasSize(5)
                .extracting("instagramMediaInfo.id")
                .containsExactly("0", "1", "2", "3", "4");
        assertThat(sut1.getContent()).hasSize(5)
                .extracting("instagramMediaInfo.id")
                .containsExactly("5", "6", "7", "8", "9");
    }

    @Test
    @DisplayName("UserId로 미디어를 페이징 조회할 수 있다.")
    void getPagedMediasByUserId() {
        //given
        userJpaRepository.save(User.builder().build());
        userJpaRepository.save(User.builder().build());
        Long userId1 = userJpaRepository.findAll().get(0).getId();
        Long userId2 = userJpaRepository.findAll().get(1).getId();
        User user1 = userJpaRepository.findById(userId1).get();
        User user2 = userJpaRepository.findById(userId2).get();

        mediaJpaRepository.saveAll(IntStream.range(0,5).mapToObj(i -> Media.builder()
                .user(user1)
                .build()).toList());
        mediaJpaRepository.saveAll(IntStream.range(0,5).mapToObj(i -> Media.builder()
                .user(user2)
                .build()).toList());

        MediaPageRequestDto mediaPageRequestDto = MediaPageRequestDto.builder()
                .page(0)
                .size(5)
                .build();

        //when
        Page<Media> sut1 = mediaService.getPagedMediasByUserId(userId1, mediaPageRequestDto);
        Page<Media> sut2 = mediaService.getPagedMediasByUserId(userId2, mediaPageRequestDto);

        //then
        assertThat(sut1.getTotalElements()).isEqualTo(5);
        assertThat(sut1.getTotalPages()).isEqualTo(1);
        assertThat(sut1.getContent()).hasSize(5)
                .extracting("user.id")
                .containsOnly(userId1);
        assertThat(sut2.getTotalElements()).isEqualTo(5);
        assertThat(sut2.getTotalPages()).isEqualTo(1);
        assertThat(sut2.getContent()).hasSize(5)
                .extracting("user.id")
                .containsOnly(userId2);
    }

    @Test
    @DisplayName("미디어 ID로 조회할 수 있다.")
    void getById() {
        //given
        mediaJpaRepository.save(Media.builder().build());
        Long mediaId = mediaJpaRepository.findAll().get(0).getId();

        //when
        Media sut = mediaService.getMediaById(mediaId);

        //then
        assertThat(sut.getId()).isEqualTo(mediaId);
    }

    private class TestInstagramMediaResponseDto extends InstagramMediaResponseDto{
        String mediaId;
        String mediaType;

        public TestInstagramMediaResponseDto() {
            super();
        }

        public TestInstagramMediaResponseDto(String mediaId, String mediaType) {
            this.mediaId = mediaId;
            this.mediaType = mediaType;
        }

        @Override
        public String getMediaId() {
            return mediaId;
        }

        @Override
        public String getMediaType() {
            return mediaType;
        }
    }
}