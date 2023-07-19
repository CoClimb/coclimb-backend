package swm.s3.coclimb.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import swm.s3.coclimb.api.exception.errortype.ValidationFail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GymTest {

    @Test
    @DisplayName("id와 이름을 제외한 필드를 null 값으로 바꾼다.")
    void remove() throws Exception {
        // given
        Gym sut = Gym.builder()
                .name("테스트 암장")
                .address("주소")
                .phone("전화번호")
                .location(Location.of(0f, 0f))
                .build();
        // when
        sut.remove();

        // then
        assertThat(sut)
                .extracting("name", "address", "phone", "location")
                .contains("테스트 암장", null, null, null);
    }

    @Test
    @DisplayName("변경 정보가 존재하는 필드를 부분 갱신한다.")
    void update() throws Exception {
        // given
        Gym sut = Gym.builder()
                .name("테스트 암장")
                .address("주소")
                .phone("전화번호")
                .location(Location.of(0f, 0f))
                .build();
        Gym updateInfo = Gym.builder()
                .name("변경 암장")
                .address("변경 주소")
                .build();

        // when
        sut.update(updateInfo);

        // then
        assertThat(sut)
                .extracting("name", "address", "phone", "location.latitude", "location.longitude")
                .contains("변경 암장", "변경 주소", "전화번호", 0f, 0f);
    }

    @ParameterizedTest
    @DisplayName("암장 이름은 필수값이다.")
    @ValueSource(strings = " ")
    @NullAndEmptySource
    void validateName(String name) throws Exception {
        // given
        Gym sut = Gym.builder()
                .name(name)
                .build();

        // when, then
        assertThatThrownBy(sut::validate)
                .isInstanceOf(ValidationFail.class)
                .hasMessage("유효성 검사를 통과하지 못한 필드가 존재합니다.")
                .extracting("fields")
                .hasFieldOrPropertyWithValue("name", "암장 이름은 필수값입니다.");
    }
}