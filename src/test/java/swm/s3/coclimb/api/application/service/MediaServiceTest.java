package swm.s3.coclimb.api.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.aws.AwsS3Manager;
import swm.s3.coclimb.api.adapter.out.filedownload.FileDownloader;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaDeleteRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaPageRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaUpdateRequestDto;
import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;
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
    @MockBean
    private FileDownloader fileDownloader;
    @MockBean
    private AwsS3Manager awsS3Manager;

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
    @DisplayName("미디어를 저장할 수 있다.")
    void save() {
        //given
        given(fileDownloader.downloadFile(any())).willReturn(DownloadedFileDetail.builder().build());
        given(awsS3Manager.uploadFile(any())).willReturn("https://test.com");

        userJpaRepository.save(User.builder().build());
        User user = userJpaRepository.findAll().get(0);

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
        Media sut = mediaJpaRepository.findByUserId(user.getId()).get(0);

        //then
        assertThat(sut.getUser().getId()).isEqualTo(user.getId());
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
        User user1 = userJpaRepository.findAll().get(0);
        User user2 = userJpaRepository.findAll().get(1);


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
        Page<Media> sut1 = mediaService.getPagedMediasByUserId(user1.getId(), mediaPageRequestDto);
        Page<Media> sut2 = mediaService.getPagedMediasByUserId(user2.getId(), mediaPageRequestDto);

        //then
        assertThat(sut1.getTotalElements()).isEqualTo(5);
        assertThat(sut1.getTotalPages()).isEqualTo(1);
        assertThat(sut1.getContent()).hasSize(5)
                .extracting("user.id")
                .containsOnly(user1.getId());
        assertThat(sut2.getTotalElements()).isEqualTo(5);
        assertThat(sut2.getTotalPages()).isEqualTo(1);
        assertThat(sut2.getContent()).hasSize(5)
                .extracting("user.id")
                .containsOnly(user2.getId());
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

    @Test
    @DisplayName("미디어 업데이트를 할 수 있다.")
    void update() {
        //given
        userJpaRepository.save(User.builder().build());
        User user = userJpaRepository.findAll().get(0);
        mediaJpaRepository.save(Media.builder().user(user).description("test").build());
        Long mediaId = mediaJpaRepository.findByUserId(user.getId()).get(0).getId();

        //when
        mediaService.updateMedia(MediaUpdateRequestDto.builder()
                .mediaId(mediaId)
                .user(user)
                .description("edit")
                .build());
        Media sut = mediaJpaRepository.findById(mediaId).get();

        //then
        assertThat(sut.getDescription()).isEqualTo("edit");
    }

    @Test
    @DisplayName("미디어를 삭제할 수 있다.")
    void delete() {
        //given
        userJpaRepository.save(User.builder().build());
        User user = userJpaRepository.findAll().get(0);
        mediaJpaRepository.save(Media.builder().user(user).build());
        Long mediaId = mediaJpaRepository.findByUserId(user.getId()).get(0).getId();

        //when
        mediaService.deleteMedia(MediaDeleteRequestDto.builder()
                .mediaId(mediaId)
                .user(user)
                .build());
        Media sut = mediaJpaRepository.findById(mediaId).orElse(null);

        //then
        assertThat(sut).isNull();
    }
}