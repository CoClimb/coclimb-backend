package swm.s3.coclimb.api.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.adapter.out.persistence.media.MediaJpaRepository;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaInfoDto;
import swm.s3.coclimb.api.application.port.out.media.MediaLoadPort;
import swm.s3.coclimb.api.application.port.out.media.MediaUpdatePort;
import swm.s3.coclimb.domain.Media;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MediaServiceTest extends IntegrationTestSupport {
    MediaService mediaService;

    @Mock
    InstagramRestApiManager instagramRestApiManager;

    @Autowired
    MediaUpdatePort mediaUpdatePort;
    @Autowired
    MediaLoadPort mediaLoadPort;
    @Autowired
    MediaJpaRepository mediaJpaRepository;

    @BeforeEach
    void setUp() {
        mediaService = new MediaService(instagramRestApiManager, mediaLoadPort, mediaUpdatePort);
    }

    @AfterEach
    void tearDown() {
        mediaJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("인스타그램 미디어 타입 중 VIDEO만 필터링하여 반환한다.")
    void getMyInstagramVideos() throws JsonProcessingException {
        //given
        given(instagramRestApiManager.getMyMedias(any(String.class))).willReturn(List.of(
                new TestInstagramMediaResponseDto("1", "VIDEO"),
                new TestInstagramMediaResponseDto("2", "IMAGE")
        ));
        //when
        List<InstagramMediaResponseDto> myVideos = mediaService.getMyInstagramVideos("accessToken");

        //then
        assertThat(myVideos).hasSize(1)
                .extracting("mediaId", "mediaType")
                .containsExactly(tuple("1", "VIDEO"));
    }

    @Test
    @DisplayName("모든 타입의 미디어를 반환한다.")
    void findAllType() {
        //given
        mediaJpaRepository.save(Media.builder().mediaType("VIDEO").build());
        mediaJpaRepository.save(Media.builder().mediaType("IMAGE").build());

        //when
        List<MediaInfoDto> allMedias = mediaService.findAll();

        //then
        assertThat(allMedias).hasSize(2)
                .extracting("mediaType")
                .containsExactly("VIDEO", "IMAGE");
    }

    @Test
    @DisplayName("비디오 타입의 미디어를 반환한다.")
    void findAllVideos() {
        //given
        mediaJpaRepository.save(Media.builder().mediaType("VIDEO").build());
        mediaJpaRepository.save(Media.builder().mediaType("IMAGE").build());

        //when
        List<MediaInfoDto> allVideos = mediaService.findAllVideos();

        //then
        assertThat(allVideos).hasSize(1)
                .extracting("mediaType")
                .containsExactly("VIDEO");
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