package swm.s3.coclimb.api.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import swm.s3.coclimb.api.adapter.in.web.gym.GymCreateRequest;
import swm.s3.coclimb.api.adapter.in.web.gym.GymRemoveRequest;
import swm.s3.coclimb.api.adapter.in.web.gym.GymUpdateRequest;
import swm.s3.coclimb.api.application.port.in.gym.GymInfoResponseDto;
import swm.s3.coclimb.api.application.port.in.gym.GymLocationResponseDto;
import swm.s3.coclimb.config.ControllerTestSupport;
import swm.s3.coclimb.domain.Location;

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

        // when, then
        mockMvc.perform(post("/gyms")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    @DisplayName("신규 암장 등록시, 암장 이름은 필수값이다.")
    void createGymWithNoName() throws Exception {
        // given
        GymCreateRequest request = GymCreateRequest.builder()
                .build();

        // when, then
        mockMvc.perform(post("/gyms")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("요청에 유효하지 않은 값이 포함된 필드가 존재합니다."))
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.name").value("암장 이름은 필수입니다."));
    }

    @Test
    @DisplayName("이름을 입력받아 해당하는 암정 정보를 제거한다.")
    void removeGymByName() throws Exception {
        // given
        GymRemoveRequest request = GymRemoveRequest.builder()
                .name("암장 이름")
                .build();

        // when, then
        mockMvc.perform(delete("/gyms")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(204))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    @DisplayName("수정할 암장 이름과 필드값을 입력받아 암장 정보를 부분 수정한다.")
    void updateGym() throws Exception {
        // given
        GymUpdateRequest request = GymUpdateRequest.builder()
                .name("대상이름")
                .updateName("수정이름")
                .build();

        // when, then
        mockMvc.perform(patch("/gyms")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(204))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()))
                .andExpect(jsonPath("$.message").isString());
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

        // when, then
        mockMvc.perform(patch("/gyms")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("요청에 유효하지 않은 값이 포함된 필드가 존재합니다."))
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.name").value("수정할 암장의 이름은 필수입니다."));
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

        // when, then
        mockMvc.perform(delete("/gyms")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("요청에 유효하지 않은 값이 포함된 필드가 존재합니다."))
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.name").value("정보를 제거할 암장의 이름은 필수입니다."));
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
        mockMvc.perform(get("/gyms/{name}","암장이름"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data.name").value("암장이름"));
    }

    @Test
    @DisplayName("이름으로 암장 정보를 조회할 때, 암장 이름은 공백일 수 없다.")
    void getGymInfoByNameWithNoWhiteSpace() throws Exception {
        // given
        String name = " ";

        // when, then
        mockMvc.perform(get("/gyms/{name}",name))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("요청에 유효하지 않은 값이 포함된 필드가 존재합니다."))
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.name").value("암장 이름은 공백일 수 없습니다."));
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

        // when
        mockMvc.perform(get("/gyms/locations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data.locations").isArray())
                .andExpect(jsonPath("$.data.count").value(2));
    }
}