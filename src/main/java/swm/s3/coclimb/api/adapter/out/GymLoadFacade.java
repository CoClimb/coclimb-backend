package swm.s3.coclimb.api.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import swm.s3.coclimb.api.adapter.out.elasticsearch.GymElasticsearchQuery;
import swm.s3.coclimb.api.adapter.out.persistence.gym.GymRepository;
import swm.s3.coclimb.api.adapter.out.persistence.gym.dto.GymNearby;
import swm.s3.coclimb.api.application.port.out.persistence.gym.GymLoadPort;
import swm.s3.coclimb.domain.gym.Gym;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class GymLoadFacade implements GymLoadPort {
    private final GymRepository gymRepository;
    private final GymElasticsearchQuery gymElasticsearchQuery;


    @Override
    public boolean existsByName(String name) {
        return gymRepository.existsByName(name);
    }

    @Override
    public Optional<Gym> findByName(String name) {
        return gymRepository.findByName(name);
    }

    @Override
    public List<Gym> findAll() {
        return gymRepository.findAll();
    }

    @Override
    public Page<Gym> findPage(PageRequest pageRequest) {
        return gymRepository.findPage(pageRequest);
    }

    @Override
    public List<GymNearby> findNearby(float latitude, float longitude, float distance) {
        return gymRepository.findNearby(latitude,longitude,distance);
    }

    @Override
    public List<Gym> searchByName(String name) {
        return gymRepository.searchByName(name);
    }

    @Override
    public List<String> autoCorrectGymNames(String name, int size) {
        return gymElasticsearchQuery.autoCorrectName(name, size);
    }
}
