package swm.s3.coclimb.api.application.service;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.elasticsearch.dto.GymElasticDto;
import swm.s3.coclimb.api.application.port.in.gym.dto.*;
import swm.s3.coclimb.api.exception.FieldErrorType;
import swm.s3.coclimb.api.exception.errortype.gym.GymNameConflict;
import swm.s3.coclimb.api.exception.errortype.gym.GymNotFound;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.gym.Location;
import swm.s3.coclimb.domain.gymlike.GymLike;
import swm.s3.coclimb.domain.user.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class GymServiceTest extends IntegrationTestSupport {
    
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
        esClient.index(i -> i.index("gyms")
                .document(GymElasticDto.fromDomain(gymJpaRepository.save(Gym.builder()
                        .name("테스트 암장")
                        .build()))));
        esClient.indices().refresh();

        // when
        GymInfoResponseDto sut = gymService.getGymInfoByName("테스트 암장");

        // then
        assertThat(sut.getName()).isEqualTo("테스트 암장");
    }


    @Test
    @DisplayName("존재하지 않는 이름으로 암장을 조회할 경우 예외가 발생한다.")
    void getGymInfoByNameWithNonexistent() throws Exception {
        // given
        esClient.index(i -> i.index("gyms")
                .document(GymElasticDto.fromDomain(Gym.builder()
                        .id(1L)
                        .name("테스트 암장")
                        .build())));
        esClient.indices().refresh();
        // when, then
        assertThatThrownBy(() -> gymService.getGymInfoByName("존재하지 않는 암장"))
                .isInstanceOf(GymNotFound.class)
                .hasMessage("해당 암장을 찾을 수 없습니다.")
                .extracting("fields")
                .hasFieldOrPropertyWithValue("name", FieldErrorType.NOT_MATCH);
    }

//    @Test
//    @DisplayName("이름으로 암장 정보를 조회한다.")
//    void getGymInfoByName() throws Exception {
//        // given
//        gymJpaRepository.save(Gym.builder()
//                .name("테스트 암장")
//                .build());
//
//        // when
//        GymInfoResponseDto sut = gymService.getGymInfoByName("테스트 암장");
//
//        // then
//        assertThat(sut.getName()).isEqualTo("테스트 암장");
//    }
//
//
//    @Test
//    @DisplayName("존재하지 않는 이름으로 암장을 조회할 경우 예외가 발생한다.")
//    void getGymInfoByNameWithNonexistent() throws Exception {
//        // given
//        gymJpaRepository.save(Gym.builder()
//                .name("테스트 암장")
//                .build());
//
//        // when, then
//        assertThatThrownBy(() -> gymService.getGymInfoByName("존재하지 않는 암장"))
//                .isInstanceOf(GymNotFound.class)
//                .hasMessage("해당 암장을 찾을 수 없습니다.")
//                .extracting("fields")
//                .hasFieldOrPropertyWithValue("name", FieldErrorType.NOT_MATCH);
//    }


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


    // 37.5454, 126.9882
    // 37.5567, 126.9709
    // 약 1.976km 지점 예시
    @Test
    @DisplayName("위치와 거리 기반으로 가까운 암장을 조회할 수 있다.")
    void getNearbyGyms() {
        // given
        gymJpaRepository.save(Gym.builder()
                .name("암장1")
                .location(Location.of(37.5454f, 126.9882f))
                .build());

        float latitude = 37.5567f;
        float longitude = 126.9709f;

        // when
        List<GymNearbyResponseDto> sut1 = gymService.getNearbyGyms(latitude, longitude, 2.0f);
        List<GymNearbyResponseDto> sut2 = gymService.getNearbyGyms(latitude, longitude, 1.0f);

        // then
        assertThat(sut1).hasSize(1)
                .extracting("name")
                .containsExactly("암장1");
        assertThat(sut2).isEmpty();
    }

    @Test
    @DisplayName("암장을 찜할 수 있다.")
    void likeGym() {
        // given
        String gymName = "gym";
        userJpaRepository.save(User.builder().build());
        gymJpaRepository.save(Gym.builder().name(gymName).build());
        User user = userJpaRepository.findAll().get(0);
        GymLikeRequestDto request = GymLikeRequestDto.builder()
                .user(user)
                .gymName(gymName)
                .build();

        // when
        gymService.likeGym(request);
        GymLike sut = gymLikeJpaRepository.findByUserIdAndGymName(user.getId(), gymName).orElse(null);

        // then
        assertThat(sut).isNotNull();
        assertThat(sut.getUser().getId()).isEqualTo(user.getId());
        assertThat(sut.getGym().getName()).isEqualTo(gymName);
    }

    @Test
    @DisplayName("찜한 암장 리스트를 조회할 수 있다.")
    void getLikedGyms() {
        // given
        User user = User.builder().build();
        Gym gym1 = Gym.builder().name("암장1").build();
        Gym gym2 = Gym.builder().name("암장2").build();
        userJpaRepository.save(user);
        gymJpaRepository.save(gym1);
        gymJpaRepository.save(gym2);
        Long userId = userJpaRepository.findAll().get(0).getId();

        gymLikeJpaRepository.saveAll(List.of(GymLike.builder().user(user).gym(gym1).build(),
                GymLike.builder().user(user).gym(gym2).build()));

        // when
        List<GymLikesResponseDto> sut = gymService.getLikedGyms(userId);

        // then
        assertThat(sut).hasSize(2)
                .extracting("name")
                .containsExactly("암장1", "암장2");
    }

    @Test
    @DisplayName("암장 찜하기를 취소할 수 있다.")
    void unlikeGym() {
        // given
        String gymName = "gym";
        userJpaRepository.save(User.builder().build());
        gymJpaRepository.save(Gym.builder().name(gymName).build());
        User user = userJpaRepository.findAll().get(0);
        Gym gym = gymJpaRepository.findByName(gymName).orElse(null);
        gymLikeJpaRepository.save(GymLike.builder().user(user).gym(gym).build());

        // when
        gymService.unlikeGym(GymUnlikeRequestDto.builder()
                .userId(user.getId())
                .gymName(gymName)
                .build());

        GymLike sut = gymLikeJpaRepository.findByUserIdAndGymName(user.getId(), gymName).orElse(null);

        // then
        assertThat(sut).isNull();
    }

    @Test
    @DisplayName("키워드를 포함하는 암장을 찾을 수 있다.")
    void searchGym() {
        // given
        String keyword1 = "더클라임";
        String keyword2 = "강남";
        String keyword3 = "클라이밍";
        gymJpaRepository.save(Gym.builder().name("더 클라임 신촌점").build());
        gymJpaRepository.save(Gym.builder().name("더 클라임 강남점").build());
        gymJpaRepository.save(Gym.builder().name("서울숲 클라이밍").build());
        gymJpaRepository.save(Gym.builder().name("강남 클라이밍").build());

        // when
        List<GymSearchResponseDto> sut1 = gymService.searchGyms(keyword1);
        List<GymSearchResponseDto> sut2 = gymService.searchGyms(keyword2);
        List<GymSearchResponseDto> sut3 = gymService.searchGyms(keyword3);

        // then
        assertThat(sut1).hasSize(2)
                .extracting("name")
                .contains("더 클라임 신촌점", "더 클라임 강남점");
        assertThat(sut2).hasSize(2)
                .extracting("name")
                .contains("더 클라임 강남점", "강남 클라이밍");
        assertThat(sut3).hasSize(2)
                .extracting("name")
                .contains("강남 클라이밍", "서울숲 클라이밍");
    }

    private static Stream<Arguments> autoCorrectName() {
        return Stream.of(
                Arguments.of("더클 서울대", "더클라임 클라이밍 짐앤샵 서울대점"),
                Arguments.of("락트리 분당", "락트리클라이밍 분당"),
                Arguments.of("서울숲 잠실", "서울숲클라이밍 잠실점"),
                Arguments.of("더클B", "더클라임 B 홍대점"),
                Arguments.of("신림 더클라임", "더클라임 클라이밍 짐앤샵 신림점"));
    }
    @ParameterizedTest
    @MethodSource
    @DisplayName("키워드를 입력 시 자동완성된 암장 이름 리스트를 size만큼 조회할 수 있다.")
    void autoCorrectName(String keyword, String expected) throws Exception{
        // given
        List<String> gymNames = List.of("더클라임 클라이밍 짐앤샵 서울대점",
                "락트리클라이밍 분당",
                "서울숲클라이밍 잠실점",
                "더클라임 B 홍대점",
                "더클라임 클라이밍 짐앤샵 신림점"
        );

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (String gymName : gymNames) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index("gyms")
                            .document(GymElasticDto.builder()
                                    .name(gymName)
                                    .build())
                    )
            );
        }
        esClient.bulk(br.build());
        esClient.indices().refresh();

        // when
        List<String> sut = gymService.autoCorrectGymNames(keyword, 1);

        System.out.println(sut);
        // then
        assertThat(sut.size()).isEqualTo(1);
        assertThat(sut.get(0)).isEqualTo(expected);
    }
}