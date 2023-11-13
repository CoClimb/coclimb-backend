package swm.s3.coclimb.api.docs;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import swm.s3.coclimb.api.RestDocsTestSupport;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateProblemInfo;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateRequest;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaUpdateRequest;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;
import swm.s3.coclimb.domain.user.User;

import java.net.URL;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MediaControllerDocsTest extends RestDocsTestSupport {
    @Value("${aws-config.s3.bucket}")
    private String bucket;

    @Test
    @DisplayName("미디어를 등록하는 API")
    void createMedia() throws Exception {
        //given

        MediaCreateRequest request = MediaCreateRequest.builder()
                .videoUrl("https://어쩌구/1/video/uuid.mp4?filed=xxx")
                .thumbnailUrl("https://어쩌구/1/thumbnail/uuid.mp4?field=xxx")
                .description("description")
                .problem(MediaCreateProblemInfo.builder()
                        .clearDate(LocalDate.now())
                        .gymName("gymName")
                        .color("color")
                        .perceivedDifficulty("perceivedDifficulty")
                        .type("problemType")
                        .isClear(true)
                        .build())
                .build();

        User user = userJpaRepository.save(User.builder().name("username").build());

        //when, then
        ResultActions result = mockMvc.perform(post("/medias")
                .header("Authorization", jwtManager.issueToken(String.valueOf(user.getId())))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        //docs
        result.andDo(document("media-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰")
                ),
                requestFields(
                        fieldWithPath("videoUrl")
                                .type(JsonFieldType.STRING)
                                .description("비디오를 업로드한 URL"),
                        fieldWithPath("thumbnailUrl")
                                .type(JsonFieldType.STRING)
                                .description("썸네일을 업로드한 URL"),
                        fieldWithPath("description")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("미디어 설명"),
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
        User user = userJpaRepository.save(User.builder().name("user").build());
        for (int iter = 0; iter < 10; iter++) {
            mediaJpaRepository.saveAll(IntStream.range(0, 2).mapToObj(i -> Media.builder()
                            .user(user)
                            .videoKey("videoKey"+i)
                            .thumbnailKey("thumbnailKey"+i)
                            .description("description")
                            .mediaProblemInfo(MediaProblemInfo.builder()
                                    .gymName("gym"+i)
                                    .color("color" + i)
                                    .clearDate(LocalDate.now())
                                    .isClear(true)
                                    .build())
                            .build())
                    .collect(Collectors.toList()));
        }

        //when
        //then
        ResultActions result = mockMvc.perform(get("/medias")
                        .param("page", "0")
                        .param("size", String.valueOf(pageSize))
                        .param("gymName", "gym0")
                        .param("userName", "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medias.length()").value(pageSize))
                .andExpect(jsonPath("$.medias[0].problem.gymName").value("gym0"))
                .andExpect(jsonPath("$.medias[1].problem.gymName").value("gym0"))
                .andExpect(jsonPath("$.medias[2].problem.gymName").value("gym0"))
                .andExpect(jsonPath("$.medias[3].problem.gymName").value("gym0"))
                .andExpect(jsonPath("$.medias[4].problem.gymName").value("gym0"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(pageSize))
                .andExpect(jsonPath("$.totalPage").value(2));


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
                        fieldWithPath("medias[].videoUrl")
                                .type(JsonFieldType.STRING)
                                .description("영상 URL"),
                        fieldWithPath("medias[].thumbnailUrl")
                                .type(JsonFieldType.STRING)
                                .description("썸네일 URL"),
                        fieldWithPath("medias[].description")
                                .type(JsonFieldType.STRING)
                                .description("미디어 설명"),
                        fieldWithPath("medias[].problem.gymName").optional()
                                .type(JsonFieldType.STRING)
                                .description("문제가 속한 암장 이름"),
                        fieldWithPath("medias[].problem.color")
                                .type(JsonFieldType.STRING)
                                .description("문제 난이도 색상"),
                        fieldWithPath("medias[].problem.clearDate")
                                .type(JsonFieldType.STRING)
                                .description("문제 클리어 날짜"),
                        fieldWithPath("medias[].problem.isClear")
                                .type(JsonFieldType.BOOLEAN)
                                .description("문제 클리어 여부"),
                        fieldWithPath("medias[].problem.perceivedDifficulty")
                                .type(JsonFieldType.STRING)
                                .description("문제 체감 난이도").optional(),
                        fieldWithPath("medias[].problem.type")
                                .type(JsonFieldType.STRING)
                                .description("문제 유형").optional()
                )));
    }

    @Test
    @DisplayName("미디어 ID로 미디어 정보 조회하는 API")
    void getMediaById() throws Exception {
        //given
        userJpaRepository.save(User.builder().name("username").build());
        User user = userJpaRepository.findAll().get(0);

        Media media = mediaJpaRepository.save(Media.builder()
                .thumbnailKey("thumbnailKey")
                .user(user)
                .videoKey("videoKey")
                .description("description")
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mediaId))
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.videoUrl").isString())
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
                        fieldWithPath("videoUrl")
                                .type(JsonFieldType.STRING)
                                .description("영상 URL"),
                        fieldWithPath("thumbnailUrl")
                                .type(JsonFieldType.STRING)
                                .description("썸네일 URL"),
                        fieldWithPath("description")
                                .type(JsonFieldType.STRING)
                                .description("미디어 설명"),
                        fieldWithPath("problem.gymName").optional()
                                .type(JsonFieldType.STRING)
                                .description("문제가 속한 암장 이름"),
                        fieldWithPath("problem.color")
                                .type(JsonFieldType.STRING)
                                .description("문제 난이도 색상"),
                        fieldWithPath("problem.clearDate")
                                .type(JsonFieldType.STRING)
                                .description("문제 클리어 날짜"),
                        fieldWithPath("problem.isClear")
                                .type(JsonFieldType.BOOLEAN)
                                .description("문제 클리어 여부"),
                        fieldWithPath("problem.perceivedDifficulty")
                                .type(JsonFieldType.STRING)
                                .description("문제 체감 난이도").optional(),
                        fieldWithPath("problem.type")
                                .type(JsonFieldType.STRING)
                                .description("문제 유형").optional()
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
        String s3Key = "test/deleteMedia.txt";
        URL uploadUrl = awsS3Manager.getUploadUrl(s3Key);
        uploadByPreSignedUrl(uploadUrl);
        User user = userJpaRepository.save(User.builder().build());
        Long mediaId = mediaJpaRepository.save(Media.builder()
                .videoKey(s3Key)
                .thumbnailKey(s3Key)
                .user(user)
                .build()).getId();

        //when, then
        ResultActions result = mockMvc.perform(delete("/medias/{id}", mediaId)
                .header("Authorization", jwtManager.issueToken(String.valueOf(user.getId()))))
                .andExpect(status().isNoContent());
        Media sut = mediaJpaRepository.findById(mediaId).orElse(null);
        assertThat(sut).isNull();
        assertThatThrownBy(()->amazonS3Client.getObject(bucket, s3Key)).isInstanceOf(AmazonS3Exception.class);

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
    @DisplayName("미디어를 업로드 할 Url을 요청하는 API")
    void getUploadUrl() throws Exception {
        // given
        User user = User.builder()
                .name("user")
                .build();
        userJpaRepository.save(user);
        String accessToken = jwtManager.issueToken(user.getId().toString());

        // when, then
        ResultActions result = mockMvc.perform(get("/medias/upload")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoUploadUrl").isString())
                .andExpect(jsonPath("$.thumbnailUploadUrl").isString());

        //docs
        result.andDo(document("media-upload-url",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("Authorization")
                                .description("JWT 인증 토큰")
                ),
                responseFields(
                        fieldWithPath("videoUploadUrl")
                                .type(JsonFieldType.STRING)
                                .description("영상을 업로드할 Url"),
                        fieldWithPath("thumbnailUploadUrl")
                                .type(JsonFieldType.STRING)
                                .description("썸네일을 업로드할 Url")
                )));

    }
}
