package swm.s3.coclimb.api.adapter.out.persistence.gym;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import swm.s3.coclimb.api.adapter.out.persistence.gym.dto.GymNearby;
import swm.s3.coclimb.api.application.port.out.persistence.gym.GymUpdatePort;
import swm.s3.coclimb.domain.gym.Gym;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GymRepository implements GymUpdatePort{
    private final GymJpaRepository gymJpaRepository;

    @Override
    public void save(Gym gym) {
        gymJpaRepository.save(gym);
    }


    public boolean existsByName(String name) {
        return gymJpaRepository.existsByName(name);
    }


    public Optional<Gym> findByName(String name) {
        return gymJpaRepository.findByName(name);
    }


    public List<Gym> findAll() {
        return gymJpaRepository.findAll();
    }


    public Page<Gym> findPage(PageRequest pageRequest){
        return gymJpaRepository.findAll(pageRequest);}


    public List<GymNearby> findNearby(float latitude, float longitude, float distance) {
        return gymJpaRepository.findNearby(latitude, longitude, distance);
    }

    public List<Gym> searchByName(String keyword) {
        return gymJpaRepository.findByNameLike(keyword);
    }
}
