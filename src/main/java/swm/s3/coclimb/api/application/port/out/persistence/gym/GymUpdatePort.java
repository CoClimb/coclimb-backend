package swm.s3.coclimb.api.application.port.out.persistence.gym;

import swm.s3.coclimb.domain.gym.Gym;

public interface GymUpdatePort {
    void save(Gym gym);
}
