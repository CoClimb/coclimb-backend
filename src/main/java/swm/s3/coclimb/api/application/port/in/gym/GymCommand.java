package swm.s3.coclimb.api.application.port.in.gym;

import org.springframework.transaction.annotation.Transactional;

public interface GymCommand {
    void createGym(GymCreateRequestDto request);

    void removeGymByName(String name);


    @Transactional
    void updateGym(GymUpdateRequestDto request);
}
