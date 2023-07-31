package swm.s3.coclimb.api.adapter.in.web.gym;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import swm.s3.coclimb.api.ControllerTestSupport;
import swm.s3.coclimb.api.adapter.in.web.gym.dto.GymCreateRequest;
import swm.s3.coclimb.api.adapter.in.web.gym.dto.GymRemoveRequest;
import swm.s3.coclimb.api.adapter.in.web.gym.dto.GymUpdateRequest;
import swm.s3.coclimb.api.application.port.in.gym.dto.GymInfoResponseDto;
import swm.s3.coclimb.api.application.port.in.gym.dto.GymLocationResponseDto;
import swm.s3.coclimb.api.exception.FieldErrorType;
import swm.s3.coclimb.domain.gym.Location;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GymControllerTest extends ControllerTestSupport{

    @Test
    @DisplayName("신규 암장을 등록한다.")
    void createGym() throws Exception {
        // given
        GymCreateRequest request = GymCreateRequest.builder()
                .name("암장 이름")
                .build();
        String accessToken = "token";
        given(jwtManager.isValid(accessToken)).willReturn(true);
        // when, then
        mockMvc.perform(post("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    @Test
    @DisplayName("신규 암장 등록시, 암장 이름은 필수값이다.")
    void createGymWithNoName() throws Exception {
        // given
        GymCreateRequest request = GymCreateRequest.builder()
                .build();
        String accessToken = "token";
        given(jwtManager.isValid(accessToken)).willReturn(true);
        // when, then
        mockMvc.perform(post("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.name").value(FieldErrorType.NOT_BLANK));
    }
    @Test
    @DisplayName("인증된 사용자만 신규 암장을 등록할 수 있다.")
    void createGymNeedAuth() throws Exception {
        // given
        GymCreateRequest request = GymCreateRequest.builder()
                .build();
        String accessToken = "invalid token";
        given(jwtManager.isValid(accessToken)).willReturn(false);
        // when, then
        mockMvc.perform(post("/gyms")
                        .header("Authorization", accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isEmpty());
    }

    @Test
    @DisplayName("이름을 입력받아 해당하는 암정 정보를 제거한다.")
    void removeGymByName() throws Exception {
        // given
        GymRemoveRequest request = GymRemoveRequest.builder()
                .name("암장 이름")
                .build();
        String accessToken = "token";
        given(jwtManager.isValid(accessToken)).willReturn(true);
        // when, then
        mockMvc.perform(delete("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
    @ParameterizedTest
    @DisplayName("암장 정보 제거 시, 제거할 암장 이름은 필수값이다.")
    @NullAndEmptySource
    @ValueSource(strings = "  ")
    void removeGymByNameWithNoName(String name) throws Exception {
        // given
        GymRemoveRequest request = GymRemoveRequest.builder()
                .name(name)
                .build();
        String accessToken = "token";
        given(jwtManager.isValid(accessToken)).willReturn(true);
        // when, then
        mockMvc.perform(delete("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.name").value(FieldErrorType.NOT_BLANK));
    }

    @Test
    @DisplayName("인증된 사용자만 암장 정보를 제거할 수 있다.")
    void removeGymNeedAuth() throws Exception {
        // given
        GymRemoveRequest request = GymRemoveRequest.builder()
                .name("암장 이름")
                .build();
        String accessToken = "invalid token";
        given(jwtManager.isValid(accessToken)).willReturn(false);
        // when, then
        mockMvc.perform(delete("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isEmpty());
    }

    @Test
    @DisplayName("수정할 암장 이름과 필드값을 입력받아 암장 정보를 부분 수정한다.")
    void updateGym() throws Exception {
        // given
        GymUpdateRequest request = GymUpdateRequest.builder()
                .name("대상이름")
                .updateName("수정이름")
                .build();
        String accessToken = "token";
        given(jwtManager.isValid(accessToken)).willReturn(true);
        // when, then
        mockMvc.perform(patch("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @DisplayName("암장 정보 수정 시, 수정할 암장 이름은 필수값이다.")
    @NullAndEmptySource
    @ValueSource(strings = "  ")
    void updateGymWithNoTargetName(String targetName) throws Exception {
        // given
        GymUpdateRequest request = GymUpdateRequest.builder()
                .name(targetName)
                .updateName("수정이름")
                .build();
        String accessToken = "token";
        given(jwtManager.isValid(accessToken)).willReturn(true);
        // when, then
        mockMvc.perform(patch("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.name").value(FieldErrorType.NOT_BLANK));
    }

    @ParameterizedTest
    @DisplayName("암장 정보 수정 시, 암장 이름은 공백으로 수정될 수 없다.")
    @ValueSource(strings = {"", "  "})
    void updateGymWithBlankUpdateName(String updateName) throws Exception {
        // given
        GymUpdateRequest request = GymUpdateRequest.builder()
                .name("암장이름")
                .updateName(updateName)
                .build();
        String accessToken = "token";
        given(jwtManager.isValid(accessToken)).willReturn(true);
        // when, then
        mockMvc.perform(patch("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.updateName").value(FieldErrorType.NOT_BLANK));
    }

    @Test
    @DisplayName("수정할 암장 이름과 필드값을 입력받아 암장 정보를 부분 수정한다.")
    void updateGymNeedAuth() throws Exception {
        // given
        GymUpdateRequest request = GymUpdateRequest.builder()
                .name("대상이름")
                .updateName("수정이름")
                .build();
        String accessToken = "invalid token";
        given(jwtManager.isValid(accessToken)).willReturn(false);
        // when, then
        mockMvc.perform(patch("/gyms")
                        .header("Authorization",accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isEmpty());
    }


    @Test
    @DisplayName("이름으로 암장 정보를 조회한다.")
    void getGymInfoByName() throws Exception {
        // given
        given(gymQuery.getGymInfoByName(any(String.class)))
                .willReturn(GymInfoResponseDto.builder()
                        .name("암장이름")
                        .build());

        // when, then
        mockMvc.perform(get("/gyms/info/{name}","암장이름"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("암장이름"));
    }

    @Test
    @DisplayName("이름으로 암장 정보를 조회할 때, 암장 이름은 공백일 수 없다.")
    void getGymInfoByNameWithNoWhiteSpace() throws Exception {
        // given
        String name = " ";

        // when, then
        mockMvc.perform(get("/gyms/info/{name}",name))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.name").value(FieldErrorType.NOT_BLANK));
        then(gymQuery).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("암장들의 위치 정보를 조회한다.")
    void getGymLocations() throws Exception {
        // given
        GymLocationResponseDto gym1 = GymLocationResponseDto.builder()
                .name("암장1")
                .location(Location.of(0f, 0f))
                .build();
        GymLocationResponseDto gym2 = GymLocationResponseDto.builder()
                .name("암장2")
                .location(Location.of(5f, -9f))
                .build();

        given(gymQuery.getGymLocations()).willReturn(List.of(gym1, gym2));

        // when, then
        mockMvc.perform(get("/gyms/locations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locations").isArray())
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    @DisplayName("기본 암장 페이지를 조회한다.")
    void getPagedGyms() throws Exception {
        // given
        given(gymQuery.getPagedGyms(any()))
                .willReturn(Page.empty(PageRequest.of(1,5)));

        // when, then
        mockMvc.perform(get("/gyms")
                        .param("page",String.valueOf(0))
                        .param("size",String.valueOf(5)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gyms").isArray())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPage").isNumber());

    }
    @Test
    @DisplayName("암장 페이지 조회 시, page 번호는 0이상의 정수여야 한다.")
    void getPagedGymsWithoutPage() throws Exception {
        // given

        given(gymQuery.getPagedGyms(any()))
                .willReturn(Page.empty(PageRequest.of(1,5)));

        // when, then
        mockMvc.perform(get("/gyms")
                        .param("page", String.valueOf(-1))
                        .param("size", String.valueOf(5)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.page").value(FieldErrorType.MIN(0)));
        then(gymQuery).shouldHaveNoInteractions();
    }
}