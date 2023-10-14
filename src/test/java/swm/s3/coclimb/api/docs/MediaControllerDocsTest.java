package swm.s3.coclimb.api.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import swm.s3.coclimb.api.RestDocsTestSupport;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateInstagramInfo;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateProblemInfo;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateRequest;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaUpdateRequest;
import swm.s3.coclimb.api.adapter.out.aws.AwsS3Manager;
import swm.s3.coclimb.api.adapter.out.filedownload.FileDownloader;
import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;
import swm.s3.coclimb.domain.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MediaControllerDocsTest extends RestDocsTestSupport {

    @MockBean
    AwsS3Manager awsS3Manager;
    @MockBean
    FileDownloader fileDownloader;

    @Test
    @DisplayName("미디어를 등록하는 API")
    void createMedia() throws Exception {
        //given
        given(fileDownloader.downloadFile(any())).willReturn(DownloadedFileDetail.builder().build());
        given(awsS3Manager.uploadFile(any())).willReturn("https://test.com");

        MediaCreateRequest request = MediaCreateRequest.builder()
                .platform("instagram")
                .mediaType("VIDEO")
                .mediaUrl("mediaUrl")
                .thumbnailUrl("thumbnailUrl")
                .description("description")
                .problem(MediaCreateProblemInfo.builder()
                        .clearDate(LocalDate.now())
                        .gymName("gymName")
                        .color("color")
                        .perceivedDifficulty("perceivedDifficulty")
                        .type("problemType")
                        .isClear(true)
                        .build())
                .instagram(MediaCreateInstagramInfo.builder()
                        .mediaId("instagramMediaId")
                        .permalink("instagramPermalink")
                        .build())
                .build();

        userJpaRepository.save(User.builder().build());
        Long userId = userJpaRepository.findAll().get(0).getId();

        //when
        ResultActions result = mockMvc.perform(post("/medias")
                .header("Authorization", jwtManager.issueToken(String.valueOf(userId)))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        Media sut = mediaJpaRepository.findByUserId(userId).get(0);

        //then
        result.andExpect(status().isCreated());
        assertThat(sut.getMediaProblemInfo().getColor()).isEqualTo("color");

        //docs
        result.andDo(document("media-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰")
                ),
                requestFields(
                        fieldWithPath("platform")
                                .type(JsonFieldType.STRING)
                                .description("미디어 플랫폼 (ex. instagram, original)"),
                        fieldWithPath("mediaType")
                                .type(JsonFieldType.STRING)
                                .description("미디어 타입"),
                        fieldWithPath("mediaUrl")
                                .type(JsonFieldType.STRING)
                                .description("1. 업로드 한 미디어가 저장된 S3 bucket의 key 혹은 2. 외부 플랫폼 미디어 CDN URL"),
                        fieldWithPath("thumbnailUrl")
                                .type(JsonFieldType.STRING)
                                .description("1. 업로드 한 썸네일이 저장된 S3 bucket의 key 혹은 2. 외부 플랫폼 썸네일 CDN URL"),
                        fieldWithPath("description")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("미디어 설명"),
                        fieldWithPath("instagram.mediaId")
                                .type(JsonFieldType.STRING)
                                .description("인스타그램 미디어 ID"),
                        fieldWithPath("instagram.permalink")
                                .type(JsonFieldType.STRING)
                                .description("인스타그램 미디어 게시물 URL"),
                        fieldWithPath("problem.gymName")
                                .type(JsonFieldType.STRING)
                                .description("미디어 내 암장명"),
                        fieldWithPath("problem.clearDate")
                                .type(JsonFieldType.STRING)
                                .description("문제 클리어 날짜"),
                        fieldWithPath("problem.color")
                                .type(JsonFieldType.STRING)
                                .description("문제 색상"),
                        fieldWithPath("problem.type")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("문제 타입"),
                        fieldWithPath("problem.perceivedDifficulty")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("체감 난이도"),
                        fieldWithPath("problem.isClear")
                                .type(JsonFieldType.BOOLEAN)
                                .description("문제 클리어 여부")
                )));
    }

    @Test
    @DisplayName("전체 미디어 페이지 조회하는 API")
    void getPagedMedias() throws Exception {
        //given
        int pageSize = 5;
        IntStream.range(0, 10).forEach(i -> userJpaRepository.save(User.builder().name("user" + String.valueOf(i)).build()));
        List<User> users = userJpaRepository.findAll();

        mediaJpaRepository.saveAll(IntStream.range(0, 10).mapToObj(i -> Media.builder()
                        .user(users.get(i))
                        .mediaUrl("mediaUrl")
                        .thumbnailUrl("thumbnailUrl" + String.valueOf(i))
                        .mediaProblemInfo(MediaProblemInfo.builder()
                                .gymName("gym" + String.valueOf(i))
                                .color("color" + String.valueOf(i))
                                .build())
                        .build())
                .collect(Collectors.toList()));

        //when
        //then
        ResultActions result = mockMvc.perform(get("/medias")
                        .param("page", "0")
                        .param("size", String.valueOf(pageSize))
                        .param("gymName", "null")
                        .param("userName", "null"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medias.length()").value(pageSize))
                .andExpect(jsonPath("$.medias[0].gymName").value("gym0"))
                .andExpect(jsonPath("$.medias[1].gymName").value("gym1"))
                .andExpect(jsonPath("$.medias[2].gymName").value("gym2"))
                .andExpect(jsonPath("$.medias[3].gymName").value("gym3"))
                .andExpect(jsonPath("$.medias[4].gymName").value("gym4"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(pageSize))
                .andExpect(jsonPath("$.totalPage").value(2));
        mockMvc.perform(get("/medias")
                .param("page", "0")
                .param("size", String.valueOf(pageSize))
                .param("gymName", "gym3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medias.length()").value(1))
                .andExpect(jsonPath("$.medias[0].gymName").value("gym3"));
        mockMvc.perform(get("/medias")
                .param("page", "0")
                .param("size", String.valueOf(pageSize))
                .param("userName", "user3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medias.length()").value(1))
                .andExpect(jsonPath("$.medias[0].username").value("user3"));
        mockMvc.perform(get("/medias")
                .param("page", "0")
                .param("size", String.valueOf(pageSize))
                .param("gymName", "gym3")
                .param("userName", "user3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medias.length()").value(1))
                .andExpect(jsonPath("$.medias[0].gymName").value("gym3"))
                .andExpect(jsonPath("$.medias[0].username").value("user3"));

        //docs
        result.andDo(document("media-page",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 사이즈"),
                        parameterWithName("gymName").description("암장 이름 (없을 시 전체 조회)"),
                        parameterWithName("userName").description("유저 이름 (없을 시 전체 조회)")
                ),
                responseFields(
                        fieldWithPath("page")
                                .type(JsonFieldType.NUMBER)
                                .description("페이지 번호"),
                        fieldWithPath("size")
                                .type(JsonFieldType.NUMBER)
                                .description("페이지 사이즈"),
                        fieldWithPath("totalPage")
                                .type(JsonFieldType.NUMBER)
                                .description("전체 페이지 수"),
                        fieldWithPath("medias")
                                .type(JsonFieldType.ARRAY)
                                .description("미디어 목록"),
                        fieldWithPath("medias[].id")
                                .type(JsonFieldType.NUMBER)
                                .description("미디어 ID"),
                        fieldWithPath("medias[].username")
                                .type(JsonFieldType.STRING)
                                .description("미디어 업로드 유저명"),
                        fieldWithPath("medias[].thumbnailUrl")
                                .type(JsonFieldType.STRING)
                                .description("미디어 썸네일 URL"),
                        fieldWithPath("medias[].gymName")
                                .type(JsonFieldType.STRING)
                                .description("미디어 내 암장 이름"),
                        fieldWithPath("medias[].problemColor")
                                .type(JsonFieldType.STRING)
                                .description("미디어 내 문제 난이도 색상")
                )));
    }

    @Test
    @DisplayName("미디어 ID로 미디어 정보 조회하는 API")
    void getMediaById() throws Exception {
        //given
        userJpaRepository.save(User.builder().name("username").build());
        User user = userJpaRepository.findAll().get(0);

        Media media = mediaJpaRepository.save(Media.builder()
                .thumbnailUrl("thumbnailUrl")
                .user(user)
                .mediaUrl("mediaUrl")
                .mediaType("VIDEO")
                .description("description")
                .platform("INSTAGRAM")
                .instagramMediaInfo(InstagramMediaInfo.builder()
                        .permalink("permalink")
                        .build())
                .mediaProblemInfo(MediaProblemInfo.builder()
                        .clearDate(LocalDate.now())
                        .gymName("gymName")
                        .color("color")
                        .isClear(true)
                        .type("type")
                        .perceivedDifficulty("perceivedDifficulty")
                        .build())
                .build());
        Long mediaId = mediaJpaRepository.findAll().get(0).getId();

        //when
        //then
        ResultActions result = mockMvc.perform(org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get("/medias/{id}", mediaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mediaId))
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.platform").value("INSTAGRAM"))
                .andExpect(jsonPath("$.mediaUrl").isString())
                .andExpect(jsonPath("$.thumbnailUrl").isString())
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.problem.clearDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.problem.gymName").value("gymName"))
                .andExpect(jsonPath("$.problem.color").value("color"))
                .andExpect(jsonPath("$.problem.isClear").value(true))
                .andExpect(jsonPath("$.problem.perceivedDifficulty").value("perceivedDifficulty"));

        //docs
        result.andDo(document("media-info",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("id").description("미디어 ID")
                ),
                responseFields(
                        fieldWithPath("id")
                                .type(JsonFieldType.NUMBER)
                                .description("미디어 ID"),
                        fieldWithPath("username")
                                .type(JsonFieldType.STRING)
                                .description("미디어 업로드 유저명"),
                        fieldWithPath("platform")
                                .type(JsonFieldType.STRING)
                                .description("미디어 플랫폼 (ex. instagram, original)"),
                        fieldWithPath("mediaUrl")
                                .type(JsonFieldType.STRING)
                                .description("미디어 URL"),
                        fieldWithPath("thumbnailUrl")
                                .type(JsonFieldType.STRING)
                                .description("미디어 썸네일 URL"),
                        fieldWithPath("description")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("미디어 설명"),
                        fieldWithPath("problem.clearDate")
                                .type(JsonFieldType.STRING)
                                .description("문제 클리어 날짜"),
                        fieldWithPath("problem.gymName")
                                .type(JsonFieldType.STRING)
                                .description("문제 암장 이름"),
                        fieldWithPath("problem.color")
                                .type(JsonFieldType.STRING)
                                .description("문제 난이도 색상"),
                        fieldWithPath("problem.type")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("문제 타입"),
                        fieldWithPath("problem.isClear")
                                .type(JsonFieldType.BOOLEAN)
                                .description("문제 클리어 여부"),
                        fieldWithPath("problem.perceivedDifficulty")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("문제 체감 난이도")
                )));
    }

    @Test
    @DisplayName("미디어 정보를 수정하는 API")
    void updateMedia() throws Exception {
        //given
        userJpaRepository.save(User.builder().build());
        User user = userJpaRepository.findAll().get(0);
        mediaJpaRepository.save(Media.builder().user(user).description("description").build());
        Long mediaId = mediaJpaRepository.findAll().get(0).getId();

        //when
        //then
        ResultActions result = mockMvc.perform(patch("/medias/{id}", mediaId)
                .header("Authorization", jwtManager.issueToken(String.valueOf(user.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(MediaUpdateRequest.builder()
                        .description("edit")
                        .build())))
                .andExpect(status().isNoContent());
        Media sut = mediaJpaRepository.findById(mediaId).orElse(null);
        assertThat(sut.getDescription()).isEqualTo("edit");

        //docs
        result.andDo(document("media-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("Authorization")
                                .description("JWT 인증 토큰")
                ),
                pathParameters(
                        parameterWithName("id")
                                .description("미디어 ID")
                ),
                requestFields(
                        fieldWithPath("description")
                                .type(JsonFieldType.STRING)
                                .description("업데이트할 본문 내용")
                )));
    }

    @Test
    @DisplayName("미디어를 삭제하는 API")
    void deleteMedia() throws Exception {
        //given
        willDoNothing().given(awsS3Manager).deleteFile(any());
        userJpaRepository.save(User.builder().build());
        User user = userJpaRepository.findAll().get(0);
        mediaJpaRepository.save(Media.builder().user(user).build());
        Long mediaId = mediaJpaRepository.findAll().get(0).getId();

        //when
        //then
        ResultActions result = mockMvc.perform(delete("/medias/{id}", mediaId)
                .header("Authorization", jwtManager.issueToken(String.valueOf(user.getId()))))
                .andExpect(status().isNoContent());
        Media sut = mediaJpaRepository.findById(mediaId).orElse(null);
        assertThat(sut).isNull();

        //docs
        result.andDo(document("media-delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("Authorization")
                                .description("JWT 인증 토큰")
                ),
                pathParameters(
                        parameterWithName("id")
                                .description("미디어 ID")
                )));
    }

    @Test
    @DisplayName("미디어 업로드를 위한 엑세스 토큰을 요청하는 API")
    void getMediaAccessToken() throws Exception {
        // given
        User user = User.builder()
                .name("user")
                .build();
        userJpaRepository.save(user);
        String accessToken = jwtManager.issueToken(user.getId().toString());

        // when, then
        ResultActions result = mockMvc.perform(get("/medias/upload-token")
                        .header("Authorization", accessToken)
                        .queryParam("type", "0"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessKey").isString())
                .andExpect(jsonPath("$.secretKey").isString())
                .andExpect(jsonPath("$.sessionToken").isString())
                .andExpect(jsonPath("$.bucket").value("coclimb-media-bucket"))
                .andExpect(jsonPath("$.key").isString());

        //docs
        result.andDo(document("media-upload-token",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("Authorization")
                                .description("JWT 인증 토큰")
                ),
                queryParameters(
                        parameterWithName("type")
                                .description("업로드 파일 타입 : 0(미디어), 1(썸네일)")
                ),
                responseFields(
                        fieldWithPath("accessKey")
                                .type(JsonFieldType.STRING)
                                .description("access key"),
                        fieldWithPath("secretKey")
                                .type(JsonFieldType.STRING)
                                .description("secret key"),
                        fieldWithPath("sessionToken")
                                .type(JsonFieldType.STRING)
                                .description("session token"),
                        fieldWithPath("bucket")
                                .type(JsonFieldType.STRING)
                                .description("s3 버켓 이름"),
                        fieldWithPath("key")
                                .type(JsonFieldType.STRING)
                                .description("파일 경로")
                )));

    }
}
