package swm.s3.coclimb.api.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import swm.s3.coclimb.api.application.port.out.GymLoadPort;
import swm.s3.coclimb.api.application.port.out.GymUpdatePort;
import swm.s3.coclimb.domain.Gym;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GymRepository implements GymUpdatePort, GymLoadPort {
    private final GymJpaRepository gymJpaRepository;

    @Override
    public void save(Gym gym) {
        gymJpaRepository.save(gym);
    }

    @Override
    public boolean existsByName(String name) {
        return gymJpaRepository.existsByName(name);
    }

    @Override
    public Optional<Gym> findByName(String name) {
        return gymJpaRepository.findByName(name);
    }

    @Override
    public List<Gym> findAll() {
        return gymJpaRepository.findAll();
    }

}
