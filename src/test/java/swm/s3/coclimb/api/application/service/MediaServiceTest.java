package swm.s3.coclimb.api.application.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.dto.*;
import swm.s3.coclimb.api.application.port.out.filestore.dto.MediaUploadUrl;
import swm.s3.coclimb.api.exception.errortype.media.InstagramMediaIdConflict;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;
import swm.s3.coclimb.domain.user.User;

import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class MediaServiceTest extends IntegrationTestSupport {

    @MockBean
    private InstagramRestApiManager instagramRestApiManager;
    @Value("${aws-config.s3.bucket}")
    private String bucket;
    @Test
    @DisplayName("인스타그램 미디어 타입 중 VIDEO만 필터링하여 반환한다.")
    void getMyInstagramVideos() {
        //given
        given(instagramRestApiManager.getMyMedias(any(String.class))).willReturn(List.of(
                InstagramMediaResponseDto.builder().mediaId("1").mediaType("VIDEO").build(),
                InstagramMediaResponseDto.builder().mediaId("2").mediaType("IMAGE").build()
        ));
        //when
        List<InstagramMediaResponseDto> sut = mediaService.getMyInstagramVideos("accessToken");

        //then
        assertThat(sut).hasSize(1)
                .extracting("mediaId", "mediaType")
                .containsExactly(tuple("1", "VIDEO"));
    }

    @Test
    @DisplayName("미디어 정보를 저장할 수 있다.")
    void save() {
        //given
        userJpaRepository.save(User.builder().build());
        User user = userJpaRepository.findAll().get(0);

        MediaCreateRequestDto mediaCreateRequestDto = MediaCreateRequestDto.builder()
                .user(user)
                .videoUrl("https://어쩌구/1/video/uuid.mp4?filed=xxx")
                .thumbnailUrl("https://어쩌구/1/thumbnail/uuid.mp4?field=xxx")
                .mediaProblemInfo(MediaProblemInfo.builder()
                        .color("problemColor")
                        .build())
                .build();

        //when
        mediaService.createMedia(mediaCreateRequestDto);
        Media sut = mediaJpaRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).get(0);

        //then
        assertThat(sut.getUser().getId()).isEqualTo(user.getId());
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
    @DisplayName("미디어 ID로 조회할 수 있다.")
    void getById() {
        //given

        User user = userJpaRepository.save(User.builder().name("username").build());
        Long mediaId = mediaJpaRepository.save(Media.builder()
                .user(user)
                .videoKey("videoKey")
                .thumbnailKey("thumbnailKey")
                .build()).getId();

        //when
        MediaInfo sut = mediaService.getMediaById(mediaId);

        //then
        assertThat(sut.getId()).isEqualTo(mediaId);
    }

    @Test
    @DisplayName("미디어 업데이트를 할 수 있다.")
    void update() {
        //given
        userJpaRepository.save(User.builder().build());
        User user = userJpaRepository.findAll().get(0);
        mediaJpaRepository.save(Media.builder().user(user).description("test").build());
        Long mediaId = mediaJpaRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).get(0).getId();

        //when
        mediaService.updateMedia(MediaUpdateRequestDto.builder()
                .mediaId(mediaId)
                .user(user)
                .description("edit")
                .build());
        Media sut = mediaJpaRepository.findById(mediaId).get();
        User sut2 = userJpaRepository.findById(user.getId()).get();

        //then
        assertThat(sut.getDescription()).isEqualTo("edit");
    }

    @Test
    @DisplayName("저장된 미디어와 그 정보를 삭제할 수 있다.")
    void delete() {
        //given
        User user = userJpaRepository.save(User.builder().build());
        String s3Key = "test/delete.txt";
        URL uploadUrl = awsS3Manager.getUploadUrl(s3Key);
        uploadByPreSignedUrl(uploadUrl);
        Media media = mediaJpaRepository.save(Media.builder().user(user)
                .videoKey(s3Key)
                .thumbnailKey(s3Key).build());


        //when
        mediaService.deleteMedia(MediaDeleteRequestDto.builder()
                .mediaId(media.getId())
                .user(user)
                .build());
        Media sut = mediaJpaRepository.findById(media.getId()).orElse(null);

        //then
        assertThatThrownBy(()->amazonS3Client.getObject(bucket, s3Key)).isInstanceOf(AmazonS3Exception.class);
        assertThat(sut).isNull();
    }

    private static Stream<Arguments> getPagedMedias() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, "userName"),
                Arguments.of("gymName", null),
                Arguments.of("gymName", "userName"));

    }
    @ParameterizedTest
    @MethodSource
    @DisplayName("암장 이름과 유저 이름으로 미디어를 조회할 수 있다.")
    void getPagedMedias(String gymName, String userName) {
        //given

        User user = userJpaRepository.save(User.builder().name("userName").build());
        Gym gym = gymJpaRepository.save(Gym.builder().name("gymName").build());

        mediaJpaRepository.saveAll(IntStream.range(0, 10)
                .mapToObj(i -> Media.builder()
                        .user(user)
                        .videoKey("videoKey")
                        .thumbnailKey("thumbnailKey")
                        .mediaProblemInfo(MediaProblemInfo.builder()
                                .gymName(gym.getName())
                                .build())
                        .build())
                .toList());

        MediaPageRequest mediaPageRequest = MediaPageRequest.builder()
                .page(0)
                .size(5)
                .gymName(gymName)
                .userName(userName)
                .build();


        //when
        Page<MediaInfo> sut = mediaService.getPagedMedias(mediaPageRequest);

        //then
        assertThat(sut.getTotalElements()).isEqualTo(10);
        assertThat(sut.getTotalPages()).isEqualTo(2);
        assertThat(sut.getNumber()).isEqualTo(0);
        assertThat(sut.getContent()).hasSize(5)
                .extracting("username", "problem.gymName")
                .containsOnly(tuple("userName", "gymName"));

    }

    @Test
    @DisplayName("특정 S3 리소스에 미디어 업로드가 가능한 Url을 생성한다..")
    void createUploadUrl() throws Exception {
        // given
        Long userId = 1L;

        // when
        MediaUploadUrl sut = mediaService.getUploadUrl(userId);

        // then
        assertThat(sut).isNotNull();
        Assertions.assertThat(sut.getVideoUploadUrl()).isNotEmpty();
        Assertions.assertThat(sut.getThumbnailUploadUrl()).isNotEmpty();
    }

}