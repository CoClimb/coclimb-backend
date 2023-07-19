package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.application.port.in.gym.*;
import swm.s3.coclimb.api.application.port.out.GymLoadPort;
import swm.s3.coclimb.api.application.port.out.GymUpdatePort;
import swm.s3.coclimb.api.exception.errortype.gym.GymNameConflict;
import swm.s3.coclimb.api.exception.errortype.gym.GymNotFound;
import swm.s3.coclimb.domain.Gym;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GymService implements GymCommand, GymQuery {

    private final GymUpdatePort gymUpdatePort;
    private final GymLoadPort gymLoadPort;

    @Override
    @Transactional
    public void createGym(GymCreateRequestDto request) {
        if (gymLoadPort.existsByName(request.getName())){
            throw new GymNameConflict();
        }
        Gym gym = request.toEntity();
        gym.validate();
        gymUpdatePort.save(gym);
    }

    @Override
    @Transactional
    public void removeGymByName(String name) {
        Gym gym = getGymByName(name);
        gym.remove();
    }


    @Override
    @Transactional
    public void updateGym(GymUpdateRequestDto request) {
        Gym gym = getGymByName(request.getTargetName());
        gym.update(request.getUpdateInfo());
    }

    @Override
    public GymInfoResponseDto getGymInfoByName(String name) {
        Gym gym = getGymByName(name);
        return GymInfoResponseDto.of(gym);
    }

    @Override
    public List<GymLocationResponseDto> getGymLocations() {
        return gymLoadPort.findAll().stream()
                .map(GymLocationResponseDto::of)
                .toList();
    }

    private Gym getGymByName(String name) {
        Gym gym = gymLoadPort.findByName(name)
                .orElseThrow(GymNotFound::new);
        return gym;
    }


}
