package swm.s3.coclimb.api.adapter.in.web.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import software.amazon.awssdk.services.sts.model.Credentials;
import swm.s3.coclimb.api.ControllerTestSupport;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateProblemInfo;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateRequest;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaUpdateRequest;
import swm.s3.coclimb.api.application.port.out.aws.dto.S3AccessToken;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;
import swm.s3.coclimb.domain.user.InstagramUserInfo;
import swm.s3.coclimb.domain.user.User;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MediaControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("미디어를 등록할 수 있다.")
    void createMedia() throws Exception {
        //given
        String accessToken = "token";
        given(jwtManager.getSubject(accessToken)).willReturn("1");
        given(userLoadPort.getById(1L)).willReturn(User.builder()
                .name("username")
                .instagramUserInfo(InstagramUserInfo.builder().build())
                .build());
        willDoNothing().given(mediaCommand).createMedia(any());

        MediaCreateRequest request = MediaCreateRequest.builder()
                .mediaType("VIDEO")
                .mediaUrl("mediaUrl")
                .platform("platform")
                .thumbnailUrl("thumbnailUrl")
                .problem(MediaCreateProblemInfo.builder().gymName("gym").build())
                .build();

        //when
        //then
        mockMvc.perform(post("/medias")
                        .header("Authorization", accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("전체 미디어를 페이징 조회할 수 있다.")
    void getPagedMedias() throws Exception {
        //given
        int pageSize = 5;

        Page<Media> page = new PageImpl<>(IntStream.range(0, pageSize).mapToObj(i -> Media.builder()
                        .user(User.builder().build())
                        .mediaProblemInfo(MediaProblemInfo.builder().gymName("암장" + String.valueOf(i)).build())
                        .build())
                .collect(Collectors.toList()));

        given(mediaQuery.getPagedMedias(any())).willReturn(page);

        //when
        //then
        mockMvc.perform(get("/medias").param("page", "0").param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medias.length()").value(pageSize))
                .andExpect(jsonPath("$.medias[0].gymName").value("암장0"))
                .andExpect(jsonPath("$.medias[1].gymName").value("암장1"))
                .andExpect(jsonPath("$.medias[2].gymName").value("암장2"))
                .andExpect(jsonPath("$.medias[3].gymName").value("암장3"))
                .andExpect(jsonPath("$.medias[4].gymName").value("암장4"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(pageSize))
                .andExpect(jsonPath("$.totalPage").value(1));
    }

    @Test
    @DisplayName("미디어 ID로 미디어 정보를 조회할 수 있다.")
    void getMediaDetail() throws Exception {
        //given
        given(mediaQuery.getMediaById(any())).willReturn(Media.builder()
                .user(User.builder().build())
                .instagramMediaInfo(InstagramMediaInfo.builder().build())
                .mediaProblemInfo(MediaProblemInfo.builder().gymName("암장1").build())
                .build());

        //when
        //then
        mockMvc.perform(get("/medias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problem.gymName").value("암장1"));
    }

    @Test
    @DisplayName("미디어를 삭제할 수 있다.")
    void deleteMedia() throws Exception {
        //given
        String accessToken = "token";
        given(jwtManager.getSubject(accessToken)).willReturn("1");
        given(userLoadPort.getById(1L)).willReturn(User.builder()
                .name("username")
                .instagramUserInfo(InstagramUserInfo.builder().build())
                .build());
        willDoNothing().given(mediaCommand).deleteMedia(any());

        //when
        //then
        mockMvc.perform(delete("/medias/1")
                        .header("Authorization", accessToken))
                .andExpect(status().isNoContent());
        then(mediaCommand).should(times(1)).deleteMedia(any());
    }

    @Test
    @DisplayName("미디어를 수정할 수 있다.")
    void updateMedia() throws Exception {
        //given
        String accessToken = "token";
        given(jwtManager.getSubject(accessToken)).willReturn("1");
        given(userLoadPort.getById(1L)).willReturn(User.builder()
                .name("username")
                .instagramUserInfo(InstagramUserInfo.builder().build())
                .build());
        willDoNothing().given(mediaCommand).updateMedia(any());

        //when
        //then
        mockMvc.perform(patch("/medias/1")
                        .header("Authorization", accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MediaUpdateRequest.builder()
                                        .description("description")
                                        .build())))
                .andExpect(status().isNoContent());
        then(mediaCommand).should(times(1)).updateMedia(any());
    }

    @Test
    @DisplayName("미디어 접근을 위한 엑세스 토큰을 획득한다.")
    void getMediaAccessToken() throws Exception {
        // given
        String accessToken = "token";
        given(jwtManager.getSubject(accessToken)).willReturn("1");
        given(userLoadPort.getById(1L)).willReturn(User.builder()
                .name("username")
                .instagramUserInfo(InstagramUserInfo.builder().build())
                .build());
        S3AccessToken s3AccessToken = S3AccessToken.of("bucket",
                "key",
                Credentials.builder()
                        .accessKeyId("ac")
                        .secretAccessKey("sc")
                        .sessionToken("st")
                        .build());

        given(mediaCommand.createS3AccessToken(any(), any(), any(),any())).willReturn(s3AccessToken);

        // when, then
        mockMvc.perform(get("/medias/upload-token")
                        .header("Authorization", accessToken)
                        .queryParam("type", "0"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessKey").value("ac"))
                .andExpect(jsonPath("$.secretKey").value("sc"))
                .andExpect(jsonPath("$.sessionToken").value("st"))
                .andExpect(jsonPath("$.bucket").value("bucket"))
                .andExpect(jsonPath("$.key").value("key"));
    }

    @Test
    @DisplayName("미디어 접근을 위한 엑세스 토큰 요청 시, 잘못된 type을 입력한 경우 예외가 발생한다.")
    void getMediaAccessTokenWithWrongType() throws Exception {
        // given
        String accessToken = "token";
        given(jwtManager.getSubject(accessToken)).willReturn("1");
        given(userLoadPort.getById(1L)).willReturn(User.builder()
                .name("username")
                .instagramUserInfo(InstagramUserInfo.builder().build())
                .build());
        S3AccessToken s3AccessToken = S3AccessToken.of("bucket",
                "key",
                Credentials.builder()
                        .accessKeyId("ac")
                        .secretAccessKey("sc")
                        .sessionToken("st")
                        .build());

        given(mediaCommand.createS3AccessToken(any(), any(), any(),any())).willReturn(s3AccessToken);

        // when, then
        mockMvc.perform(get("/medias/access-token")
                        .header("Authorization", accessToken)
                        .queryParam("type", "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isMap());
    }
}