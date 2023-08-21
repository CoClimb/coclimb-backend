package swm.s3.coclimb.api.adapter.out.persistence.gym;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import swm.s3.coclimb.api.adapter.out.persistence.gym.dto.GymNearby;
import swm.s3.coclimb.api.application.port.out.persistence.gym.GymLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.gym.GymUpdatePort;
import swm.s3.coclimb.api.exception.errortype.gym.GymNotFound;
import swm.s3.coclimb.domain.gym.Gym;

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

    @Override
    public Page<Gym> findPage(PageRequest pageRequest){
        return gymJpaRepository.findAll(pageRequest);}

    @Override
    public List<GymNearby> findNearby(float latitude, float longitude, float distance) {
        return gymJpaRepository.findNearby(latitude, longitude, distance);
    }

    @Override
    public Gym getById(Long id) {
        return gymJpaRepository.findById(id).orElseThrow(GymNotFound::new);
    }

    @Override
    public List<Gym> searchByName(String keyword) {
        return gymJpaRepository.findByNameLike(keyword);
    }
}
