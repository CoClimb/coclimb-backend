package swm.s3.coclimb.api.docs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import swm.s3.coclimb.api.RestDocsTestSupport;
import swm.s3.coclimb.api.adapter.in.web.gym.dto.GymCreateRequest;
import swm.s3.coclimb.api.adapter.in.web.gym.dto.GymRemoveRequest;
import swm.s3.coclimb.api.adapter.in.web.gym.dto.GymUpdateRequest;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymJpaRepository;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.gym.Location;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GymControllerDocsTest extends RestDocsTestSupport {

    @Autowired GymJpaRepository gymJpaRepository;

    @AfterEach
    void tearDown() {
        gymJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("신규 암장을 등록하는 API")
    void createGym() throws Exception {
        // given
        GymCreateRequest request = GymCreateRequest.builder()
                .name("암장 이름")
                .address("주소")
                .phone("02-000-0000")
                .location(Location.of(0f,0f))
                .build();

        // when, then
        ResultActions result = mockMvc.perform(post("/gyms")
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
                                .optional()
                                .description("암장 주소"),
                        fieldWithPath("phone").type(JsonFieldType.STRING)
                                .optional()
                                .description("암장 연락처"),
                        fieldWithPath("location").type(JsonFieldType.OBJECT)
                                .optional()
                                .description("암장 위치"),
                        fieldWithPath("location.latitude").type(JsonFieldType.NUMBER)
                                .optional()
                                .description("위도"),
                        fieldWithPath("location.longitude").type(JsonFieldType.NUMBER)
                                .optional()
                                .description("경도")
                )
        ));
    }
    private void createTestGym(String name) {
        gymJpaRepository.save(Gym.builder()
                .name(name)
                .address("주소")
                .phone("02-000-0000")
                .location(Location.of(0f,0f))
                .build());
    }
    @Test
    @DisplayName("이름을 입력받아 암장 정보를 제거하는 API")
    void removeGymByName() throws Exception {
        // given
        createTestGym("암장 이름");
        GymRemoveRequest request = GymRemoveRequest.builder()
                .name("암장 이름")
                .build();


        // when, then
        ResultActions result = mockMvc.perform(delete("/gyms")
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

        // when, then
        ResultActions result = mockMvc.perform(patch("/gyms")
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
        ResultActions result = mockMvc.perform(get("/gyms/{name}", "암장이름"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("암장이름"));

        result.andDo(document("gym-info",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("name").description("조회할 암장 이름")
                ),
                responseFields(
                        fieldWithPath("name").type(JsonFieldType.STRING)
                                .description("암장 이름"),
                        fieldWithPath("address").type(JsonFieldType.STRING)
                                .description("주소"),
                        fieldWithPath("phone").type(JsonFieldType.STRING)
                                .description("연락처")
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
                        fieldWithPath("locations").type(JsonFieldType.ARRAY)
                                .description("암장 위치 정보"),
                        fieldWithPath("locations[].name").type(JsonFieldType.STRING)
                                .description("암장 이름"),
                        fieldWithPath("locations[].location").type(JsonFieldType.OBJECT)
                                .description("위치"),
                        fieldWithPath("locations[].location.latitude").type(JsonFieldType.NUMBER)
                                .description("위도"),
                        fieldWithPath("locations[].location.longitude").type(JsonFieldType.NUMBER)
                                .description("경도"),
                        fieldWithPath("count").type(JsonFieldType.NUMBER)
                                .description("조회된 암장의 수")
                )
        ));
    }
}