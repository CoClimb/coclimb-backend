package swm.s3.coclimb.api.application.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymJpaRepository;
import swm.s3.coclimb.api.application.port.in.gym.dto.*;
import swm.s3.coclimb.api.exception.FieldErrorType;
import swm.s3.coclimb.api.exception.errortype.gym.GymNameConflict;
import swm.s3.coclimb.api.exception.errortype.gym.GymNotFound;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.gym.Location;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

class GymServiceTest extends IntegrationTestSupport {

    @Autowired GymJpaRepository gymJpaRepository;
    @Autowired GymService gymService;
    
    @AfterEach
    void tearDown() {
        gymJpaRepository.deleteAllInBatch();
    }
    
    @Test
    @DisplayName("신규 암장을 등록한다.")
    void createGym() throws Exception {
        // given
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name("테스트암장")
                .build();

        // when
        gymService.createGym(request);

        // then
        List<Gym> sut = gymJpaRepository.findAll();
        assertThat(sut).hasSize(1)
                .extracting("name")
                .containsExactly("테스트암장");
    }

    @Test
    @DisplayName("신규 암장 등록 시, 이미 존재하는 암장 이름은 사용할 수 없다.")
    void createGymWithDuplicate() throws Exception {
        // given
        gymJpaRepository.save(Gym.builder()
                .name("테스트암장")
                .build());
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name("테스트암장")
                .build();
        // when, then
        assertThatThrownBy(() -> gymService.createGym(request))
                .isInstanceOf(GymNameConflict.class)
                .hasMessage("해당 이름의 암장이 이미 존재합니다.")
                .extracting("fields")
                .hasFieldOrPropertyWithValue("name", FieldErrorType.DUPLICATED);
    }

    @Test
    @DisplayName("암장명을 입력받아 암장명을 제외한 해당하는 이름의 암장 정보를 제거한다.")
    void removeGymByName() throws Exception {
        // given
        gymJpaRepository.save(Gym.builder()
                .name("테스트 암장")
                .address("주소")
                .phone("전화번호")
                .location(Location.of(0f, 0f))
                .build());

        // when
        gymService.removeGymByName("테스트 암장");

        // then
        Optional<Gym> sut = gymJpaRepository.findByName("테스트 암장");
        assertThat(sut).isNotNull();
        assertThat(sut.get())
                .extracting("name", "address", "phone", "location")
                .contains("테스트 암장", null, null, null);

    }

    @Test
    @DisplayName("변경할 암장 정보를 입력받아 암장 정보를 부분 수정한다.")
    void updateGym() throws Exception {
        // given
        gymJpaRepository.save(Gym.builder()
                .name("테스트 암장")
                .address("주소")
                .phone("전화번호")
                .location(Location.of(0f, 0f))
                .build());
        Gym updateInfo = Gym.builder()
                .name("변경 암장")
                .address("변경 주소")
                .build();
        GymUpdateRequestDto request = GymUpdateRequestDto.builder()
                .targetName("테스트 암장")
                .updateInfo(updateInfo)
                .build();
        // when
        gymService.updateGym(request);

        // then
        List<Gym> sut = gymJpaRepository.findAll();
        assertThat(sut).hasSize(1)
                .extracting("name", "address", "phone", "location.latitude", "location.longitude")
                .containsExactlyInAnyOrder(
                        tuple("변경 암장", "변경 주소", "전화번호", 0f, 0f)
                );
    }

    @Test
    @DisplayName("이름으로 암장 정보를 조회한다.")
    void getGymInfoByName() throws Exception {
        // given
        gymJpaRepository.save(Gym.builder()
                .name("테스트 암장")
                .build());

        // when
        GymInfoResponseDto sut = gymService.getGymInfoByName("테스트 암장");

        // then
        assertThat(sut.getName()).isEqualTo("테스트 암장");
    }

    @Test
    @DisplayName("존재하지 않는 이름으로 암장을 조회할 경우 예외가 발생한다.")
    void getGymInfoByNameWithNonexistent() throws Exception {
        // given
        gymJpaRepository.save(Gym.builder()
                .name("테스트 암장")
                .build());

        // when, then
        assertThatThrownBy(() -> gymService.getGymInfoByName("존재하지 않는 암장"))
                .isInstanceOf(GymNotFound.class)
                .hasMessage("해당 암장을 찾을 수 없습니다.")
                .extracting("fields")
                .hasFieldOrPropertyWithValue("name", FieldErrorType.NOT_MATCH);
    }
    
    @Test
    @DisplayName("암장들의 위치 정보를 조회한다.")
    void getGymLocations() throws Exception {
        // given
        Gym gym1 = Gym.builder()
                .name("암장1")
                .location(Location.of(0f, 0f))
                .build();
        Gym gym2 = Gym.builder()
                .name("암장2")
                .location(Location.of(5f, -9f))
                .build();
        gymJpaRepository.saveAll(List.of(gym1, gym2));

        // when
        List<GymLocationResponseDto> sut = gymService.getGymLocations();

        // then
        assertThat(sut).hasSize(2)
                .extracting("name", "location.latitude", "location.longitude")
                .containsExactlyInAnyOrder(
                        tuple("암장1", 0f, 0f),
                        tuple("암장2", 5f, -9f)
                );
    }

    @Test
    @DisplayName("페이지와 사이즈를 입력받으면 암장 페이지를 조회한다.")
    void getPagedGyms() throws Exception {
        // given
        gymJpaRepository.saveAll(IntStream.range(0, 10)
                .mapToObj(i -> Gym.builder().name("암장" + i).build())
                .toList());
        GymPageRequestDto request = GymPageRequestDto.builder()
                .page(1)
                .size(5)
                .build();
        // when
        Page<Gym> sut = gymService.getPagedGyms(request);

        // then
        assertThat(sut.getNumber()).isEqualTo(1);
        assertThat(sut.getTotalPages()).isEqualTo(2);
        assertThat(sut).hasSize(5)
                .extracting("name")
                .containsExactly("암장5", "암장6", "암장7", "암장8", "암장9");
    }

}