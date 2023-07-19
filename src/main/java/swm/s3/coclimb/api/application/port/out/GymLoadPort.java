package swm.s3.coclimb.api.application.port.out;

import swm.s3.coclimb.domain.Gym;

import java.util.List;
import java.util.Optional;

public interface GymLoadPort {
    boolean existsByName(String name);

    Optional<Gym> findByName(String name);

    List<Gym> findAll();
}
