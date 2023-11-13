package swm.s3.coclimb.api.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import swm.s3.coclimb.api.RestDocsTestSupport;
import swm.s3.coclimb.api.adapter.in.web.gym.dto.*;
import swm.s3.coclimb.domain.document.GymDocument;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.gym.Location;
import swm.s3.coclimb.domain.gymlike.GymLike;
import swm.s3.coclimb.domain.user.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GymControllerDocsTest extends RestDocsTestSupport {

    @Test
    @DisplayName("신규 암장을 등록하는 API")
    void createGym() throws Exception {
        // given
        GymCreateRequest request = GymCreateRequest.builder()
                .name("암장 이름")
                .address("주소")
                .phone("02-000-0000")
                .location(Location.of(0f,0f))
                .imageUrl("https://image.com")
                .homepageUrl("https://homepage.com")
                .instagramId("instagramId")
                .gradingSystem("gradingSystem")
                .build();
        String accessToken = jwtManager.issueToken("docs");

        // when, then
        ResultActions result = mockMvc.perform(post("/gyms")
                        .header("Authorization", accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());

        // docs
        result.andDo(document("gym-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING)
                                .description("암장 이름"),
                        fieldWithPath("address").type(JsonFieldType.STRING)
                                .description("암장 주소"),
                        fieldWithPath("phone").type(JsonFieldType.STRING)
                                .optional()
                                .description("암장 연락처"),
                        fieldWithPath("location").type(JsonFieldType.OBJECT)
                                .description("암장 위치"),
                        fieldWithPath("location.latitude").type(JsonFieldType.NUMBER)
                                .description("위도"),
                        fieldWithPath("location.longitude").type(JsonFieldType.NUMBER)
                                .description("경도"),
                        fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                .optional()
                                .description("암장 대표 이미지 URL"),
                        fieldWithPath("homepageUrl").type(JsonFieldType.STRING)
                                .optional()
                                .description("암장 홈페이지 URL"),
                        fieldWithPath("instagramId").type(JsonFieldType.STRING)
                                .optional()
                                .description("암장 인스타그램 ID"),
                        fieldWithPath("gradingSystem").type(JsonFieldType.STRING)
                                .optional()
                                .description("암장 난이도 체계")
                )
        ));
    }
    private void createTestGym(String name) {
        Gym gym = Gym.builder()
                .name(name)
                .address("주소")
                .phone("02-000-0000")
                .imageUrl("https://image.com")
                .instagramId("instagramId")
                .homepageUrl("https://homepage.com")
                .gradingSystem("gradingSystem")
                .location(Location.of(0f, 0f))
                .build();

        gymDocumentRepository.save(GymDocument.fromDomain(gymJpaRepository.save(gym)));
    }
    @Test
    @DisplayName("이름을 입력받아 암장 정보를 제거하는 API")
    void removeGymByName() throws Exception {
        // given
        createTestGym("암장 이름");
        GymRemoveRequest request = GymRemoveRequest.builder()
                .name("암장 이름")
                .build();
        String accessToken = jwtManager.issueToken("docs");

        // when, then
        ResultActions result = mockMvc.perform(delete("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());

        // docs
        result.andDo(document("gym-remove",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING)
                                .description("정보를 제거할 암장 이름")
                )
        ));
    }


    @Test
    @DisplayName("암장 정보를 수정하는 API")
    void updateGym() throws Exception {
        // given
        createTestGym("대상이름");
        GymUpdateRequest request = GymUpdateRequest.builder()
                .name("대상이름")
                .updateName("수정이름")
                .updateAddress("수정주소")
                .updatePhone("02-000-0000")
                .updateLocation(Location.of(1f,1f))
                .build();
        String accessToken = jwtManager.issueToken("docs");
        // when, then
        ResultActions result = mockMvc.perform(patch("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());

        // docs
        result.andDo(document("gym-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING)
                                .description("정보를 수정할 암장 이름"),
                        fieldWithPath("updateName").type(JsonFieldType.STRING)
                                .optional().description("이름 변경 정보"),
                        fieldWithPath("updateAddress").type(JsonFieldType.STRING)
                                .optional().description("주소 변경 정보"),
                        fieldWithPath("updatePhone").type(JsonFieldType.STRING)
                                .optional().description("연락처 변경 정보"),
                        fieldWithPath("updateLocation").type(JsonFieldType.OBJECT)
                                .optional().description("위치 변경 정보"),
                        fieldWithPath("updateLocation.latitude").type(JsonFieldType.NUMBER)
                                .optional().description("위도"),
                        fieldWithPath("updateLocation.longitude").type(JsonFieldType.NUMBER)
                                .optional().description("경도")
                        )
        ));
    }



    @Test
    @DisplayName("이름으로 암장 정보를 조회하는 API")
    void getGymInfoByName() throws Exception {
        // given
        createTestGym("암장이름");

        // when, then
        ResultActions result = mockMvc.perform(get("/gyms/info/{name}", "암장이름"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("암장이름"))
                .andExpect(jsonPath("$.gradingSystem").value("gradingSystem"));

        result.andDo(document("gym-info",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("name").description("조회할 암장 이름")
                ),
                responseFields(
                        fieldWithPath("name")
                                .type(JsonFieldType.STRING)
                                .description("암장 이름"),
                        fieldWithPath("address")
                                .type(JsonFieldType.STRING)
                                .description("주소"),
                        fieldWithPath("phone")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("연락처"),
                        fieldWithPath("imageUrl")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("대표 이미지 URL"),
                        fieldWithPath("instagramId")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("인스타그램 ID"),
                        fieldWithPath("homepageUrl")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("홈페이지 URL"),
                        fieldWithPath("gradingSystem")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("매장 난이도 분류 체계")
                )
        ));

    }
    @Test
    @DisplayName("암장들의 위치 정보를 조회하는 API")
    void getGymLocations() throws Exception {
        // given
        createTestGym("암장1");
        createTestGym("암장2");

        // when, then
        ResultActions result = mockMvc.perform(get("/gyms/locations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locations").isArray())
                .andExpect(jsonPath("$.count").value(2));

        // docs
        result.andDo(document("gym-locations",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("locations")
                                .type(JsonFieldType.ARRAY)
                                .description("암장 위치 정보"),
                        fieldWithPath("locations[].name")
                                .type(JsonFieldType.STRING)
                                .description("암장 이름"),
                        fieldWithPath("locations[].location")
                                .type(JsonFieldType.OBJECT)
                                .description("위치"),
                        fieldWithPath("locations[].location.latitude")
                                .type(JsonFieldType.NUMBER)
                                .description("위도"),
                        fieldWithPath("locations[].location.longitude")
                                .type(JsonFieldType.NUMBER)
                                .description("경도"),
                        fieldWithPath("count").type(JsonFieldType.NUMBER)
                                .description("조회된 암장의 수")
                )
        ));
    }

    @Test
    @DisplayName("페이징된 암장들을 조회하는 API")
    void getPagedGyms() throws Exception {
        // given
        IntStream.range(0, 10)
                .forEach(i -> createTestGym("암장" + i));

        // when, then
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/gyms")
                        .param("page", String.valueOf(1))
                        .param("size", String.valueOf(5)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gyms").isArray())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPage").value(2));

        // docs
        result.andDo(document("gym-page",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 크기")
                ),
                responseFields(
                        fieldWithPath("gyms")
                                .type(JsonFieldType.ARRAY)
                                .description("페이지 포함된 암장들"),
                        fieldWithPath("gyms[].name")
                                .type(JsonFieldType.STRING)
                                .description("암장 이름"),
                        fieldWithPath("gyms[].address")
                                .type(JsonFieldType.STRING)
                                .description("주소"),
                        fieldWithPath("gyms[].phone")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("전화번호"),
                        fieldWithPath("gyms[].imageUrl")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("대표 이미지"),
                        fieldWithPath("page")
                                .type(JsonFieldType.NUMBER)
                                .description("페이지 번호"),
                        fieldWithPath("size")
                                .type(JsonFieldType.NUMBER)
                                .description("페이지 크기"),
                        fieldWithPath("totalPage")
                                .type(JsonFieldType.NUMBER)
                                .description("전체 페이지 수")
                )

        ));
    }

    @Test
    @DisplayName("경도, 위도, 거리를 기반으로 가까운 암장을 찾는 API")
    void getNearbyGyms() throws Exception {
        //given
        gymJpaRepository.saveAll(IntStream.range(0, 10)
                .mapToObj(i -> Gym.builder()
                        .name("암장" + i)
                        .address("주소" + i)
                        .imageUrl("https://image" + i + ".com")
                        .location(Location.of((34f + (float)i/100f), 127f + (float)i/100f))
                        .instagramId("instagram" + i)
                        .phone("010-1234-567" + i)
                        .build())
                .toList());

        // when
        // then
        ResultActions result = mockMvc.perform(get("/gyms/nearby")
                        .param("latitude", "34.005")
                        .param("longitude", "127.005")
                        .param("distance", "5.2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gyms").isArray())
                .andExpect(jsonPath("$.count").value(5));

        // docs
        result.andDo(document("gym-nearby",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("latitude").description("위도"),
                        parameterWithName("longitude").description("경도"),
                        parameterWithName("distance").description("거리 (단위 : km, 최대 : 15)")
                ),
                responseFields(
                        fieldWithPath("gyms").type(JsonFieldType.ARRAY)
                                .description("가까운 암장들 (거리 정렬)"),
                        fieldWithPath("gyms[].name")
                                .type(JsonFieldType.STRING)
                                .description("암장 이름"),
                        fieldWithPath("gyms[].imageUrl")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("암장 대표 이미지 URL"),
                        fieldWithPath("gyms[].address")
                                .type(JsonFieldType.STRING)
                                .description("주소"),
                        fieldWithPath("gyms[].phone")
                                .type(JsonFieldType.STRING)
                                .description("전화번호"),
                        fieldWithPath("gyms[].instagramId")
                                .type(JsonFieldType.STRING)
                                .description("인스타그램 ID"),
                        fieldWithPath("gyms[].distance")
                                .type(JsonFieldType.NUMBER)
                                .description("거리 (km)"),
                        fieldWithPath("count").type(JsonFieldType.NUMBER)
                                .description("조회된 암장의 수")
                )));
    }

    @Test
    @DisplayName("암장을 찜하는 API")
    void likeGym() throws Exception {
        // given
        String gymName = "gym";
        Gym gym = Gym.builder().name(gymName).build();
        userJpaRepository.save(User.builder().build());
        gymJpaRepository.save(gym);
        User user = userJpaRepository.findAll().get(0);
        GymLikeRequest request = GymLikeRequest.builder()
                .name(gymName)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/gyms/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", jwtManager.issueToken(String.valueOf(user.getId()))))
                .andDo(print());
        GymLike gymLike = gymLikeJpaRepository.findByUserIdAndGymName(user.getId(), gymName).orElse(null);

        // then
        result.andExpect(status().isCreated());
        assertThat(gymLike).isNotNull();

        // docs
        result.andDo(document("gym-like",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰")
                ),
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING)
                                .description("암장명")
                )));
    }

    @Test
    @DisplayName("암장 찜하기를 취소하는 API")
    void unlikeGym() throws Exception {
        // given
        String gymName = "gym";
        Gym gym = Gym.builder().name(gymName).build();
        userJpaRepository.save(User.builder().build());
        gymJpaRepository.save(gym);
        User user = userJpaRepository.findAll().get(0);
        gymLikeJpaRepository.save(GymLike.builder().user(user).gym(gym).build());
        GymUnlikeRequest request = GymUnlikeRequest.builder()
                .name(gymName)
                .build();

        // when
        ResultActions result = mockMvc.perform(delete("/gyms/likes")
                        .header("Authorization", jwtManager.issueToken(String.valueOf(user.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        GymLike gymLike = gymLikeJpaRepository.findByUserIdAndGymName(user.getId(), gymName).orElse(null);

        // then
        result.andExpect(status().isNoContent());
        assertThat(gymLike).isNull();

        // docs
        result.andDo(document("gym-unlike",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰")
                ),
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING)
                                .description("암장명")
                )));
    }

    @Test
    @DisplayName("찜한 암장을 조회하는 API")
    void retrieveLikedGyms() throws Exception {
        // given
        userJpaRepository.save(User.builder().build());
        List<Gym> gyms = IntStream.range(0, 3).mapToObj(i -> Gym.builder()
                        .name("암장" + i)
                        .address("주소" + i)
                        .phone("010-1234-567" + i)
                        .instagramId("instagram" + i)
                        .imageUrl("http://image" + i + ".com")
                        .location(Location.builder()
                                .latitude(37.123456F + (float)i/1000)
                                .longitude(127.123456F + (float)i/1000)
                                .build())
                        .build())
                .collect(Collectors.toList());
        gymJpaRepository.saveAll(gyms);
        User user = userJpaRepository.findAll().get(0);
        gymLikeJpaRepository.saveAll(IntStream.range(0,3).mapToObj(i -> GymLike.builder()
                        .user(user)
                        .gym(gyms.get(i))
                        .build())
                .collect(Collectors.toList()));

        // when
        // then
        ResultActions result1 = mockMvc.perform(get("/gyms/likes")
                        .param("resultContent", "0")
                        .header("Authorization", jwtManager.issueToken(String.valueOf(user.getId()))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gyms").isArray())
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.gyms[*].name").value(containsInAnyOrder("암장0", "암장1", "암장2")));

        ResultActions result2 = mockMvc.perform(get("/gyms/likes")
                        .param("resultContent", "1")
                        .header("Authorization", jwtManager.issueToken(String.valueOf(user.getId()))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gyms").isArray())
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.gyms[*]").value(containsInAnyOrder("암장0", "암장1", "암장2")));
        // docs
        result1.andDo(document("gym-my-like-0",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("resultContent")
                                .description("결과 형태 (0: 모든 정보, 1: 암장 이름만)")
                                .optional()
                ),
                requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰")
                ),
                responseFields(
                        fieldWithPath("gyms")
                                .type(JsonFieldType.ARRAY)
                                .description("찜한 암장들"),
                        fieldWithPath("gyms[].name")
                                .type(JsonFieldType.STRING)
                                .description("이름"),
                        fieldWithPath("gyms[].address")
                                .type(JsonFieldType.STRING)
                                .description("주소"),
                        fieldWithPath("gyms[].phone")
                                .type(JsonFieldType.STRING)
                                .description("전화번호"),
                        fieldWithPath("gyms[].instagramId")
                                .type(JsonFieldType.STRING)
                                .description("인스타그램 ID"),
                        fieldWithPath("gyms[].imageUrl")
                                .type(JsonFieldType.STRING)
                                .description("대표 이미지 URL"),
                        fieldWithPath("gyms[].location.latitude")
                                .type(JsonFieldType.NUMBER)
                                .description("위도"),
                        fieldWithPath("gyms[].location.longitude")
                                .type(JsonFieldType.NUMBER)
                                .description("경도"),
                        fieldWithPath("count")
                                .type(JsonFieldType.NUMBER)
                                .description("조회된 암장의 수")
                )));

        result2.andDo(document("gym-my-like-1",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("gyms")
                                .type(JsonFieldType.ARRAY)
                                .description("찜한 암장명들"),
                        fieldWithPath("count")
                                .type(JsonFieldType.NUMBER)
                                .description("조회된 암장의 수")
                )));
    }

    @Test
    @DisplayName("암장을 검색하는 API")
    void searchGyms() throws Exception {
        // given
        gymJpaRepository.save(Gym.builder().name("더 클라이밍 강남점").build());
        gymJpaRepository.save(Gym.builder().name("더 클라이밍 홍대점").build());
        gymJpaRepository.save(Gym.builder().name("서울숲 클라이밍 본점").build());
        gymJpaRepository.save(Gym.builder().name("서울숲 클라이밍 서초점").build());
        gymJpaRepository.save(Gym.builder().name("클라이밍 파크 강남점").build());
        gymJpaRepository.save(Gym.builder().name("클라이밍 파크 연남점").build());

        // when
        // then
        ResultActions result = mockMvc.perform(get("/gyms/demo/search")
                .param("keyword", "더클라이밍"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gyms").isArray())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.gyms[*].name").value(containsInAnyOrder("더 클라이밍 강남점", "더 클라이밍 홍대점")));

        // docs
        result.andDo(document("gym-demo-search",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("keyword").description("암장 검색어")
                ),
                responseFields(
                        fieldWithPath("gyms").type(JsonFieldType.ARRAY)
                                .description("검색된 암장들"),
                        fieldWithPath("gyms[].name").type(JsonFieldType.STRING)
                                .description("암장 이름"),
                        fieldWithPath("count").type(JsonFieldType.NUMBER)
                                .description("조회된 암장의 수")
                )));
    }

}