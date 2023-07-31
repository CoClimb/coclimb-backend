package swm.s3.coclimb.api.application.port.in.gym;

import swm.s3.coclimb.api.application.port.in.gym.dto.GymCreateRequestDto;
import swm.s3.coclimb.api.application.port.in.gym.dto.GymUpdateRequestDto;

public interface GymCommand {
    void createGym(GymCreateRequestDto request);

    void removeGymByName(String name);


    void updateGym(GymUpdateRequestDto request);
}
