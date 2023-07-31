package swm.s3.coclimb.api.adapter.out.persistence.gym;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.domain.gym.Gym;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GymRepositoryTest extends IntegrationTestSupport {

    @AfterEach
    void tearDown() {
        gymJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("해당하는 이름의 암장이 존재하는지 확인한다.")
    void existsByName() throws Exception {
        // given
        gymJpaRepository.save(Gym.builder()
                .name("존재하는 암장")
                .build());

        // when, then
        assertThat(gymRepository.existsByName("존재하는 암장")).isTrue();
        assertThat(gymRepository.existsByName("존재하지 않는 암장")).isFalse();
    }

    @Test
    @DisplayName("해당하는 이름의 암장을 조회한다.")
    void findByName() throws Exception {
        // given
        gymJpaRepository.save(Gym.builder()
                .name("테스트 암장")
                .build());

        // when
        Optional<Gym> sut = gymRepository.findByName("테스트 암장");

        // then
        assertThat(sut).isNotNull();
        assertThat(sut.get().getName()).isEqualTo("테스트 암장");
    }
}