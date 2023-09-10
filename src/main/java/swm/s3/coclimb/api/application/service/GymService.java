package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.adapter.out.persistence.gym.dto.GymNearby;
import swm.s3.coclimb.api.application.port.in.gym.GymCommand;
import swm.s3.coclimb.api.application.port.in.gym.GymQuery;
import swm.s3.coclimb.api.application.port.in.gym.dto.*;
import swm.s3.coclimb.api.application.port.out.persistence.gym.GymLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.gym.GymUpdatePort;
import swm.s3.coclimb.api.application.port.out.persistence.gymlike.GymLikeLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.gymlike.GymLikeUpdatePort;
import swm.s3.coclimb.api.exception.errortype.gym.GymNameConflict;
import swm.s3.coclimb.api.exception.errortype.gym.GymNotFound;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.gymlike.GymLike;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GymService implements GymCommand, GymQuery {

    private final GymUpdatePort gymUpdatePort;
    private final GymLoadPort gymLoadPort;
    private final GymLikeUpdatePort gymLikeUpdatePort;
    private final GymLikeLoadPort gymLikeLoadPort;

    @Override
    @Transactional
    public void createGym(GymCreateRequestDto request) {
        if (gymLoadPort.existsByName(request.getName())){
            throw new GymNameConflict();
        }
        gymUpdatePort.save(request.toEntity());
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

    @Override
    public Page<Gym> getPagedGyms(GymPageRequestDto request) {
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize());
        //TODO 정렬
        return gymLoadPort.findPage(pageRequest);
    }

    @Override
    public List<GymNearbyResponseDto> getNearbyGyms(float latitude, float longitude, float distance) {
        List<GymNearby> gyms = gymLoadPort.findNearby(latitude, longitude, distance);

        return gyms.stream()
                .map(GymNearbyResponseDto::of)
                .toList();
    }

    @Override
    @Transactional
    public void likeGym(GymLikeRequestDto request) {
        Gym gym = gymLoadPort.findByName(request.getGymName()).orElseThrow(GymNotFound::new);
        gymLikeUpdatePort.save(GymLike.builder().user(request.getUser()).gym(gym).build());
    }

    @Override
    public List<GymLikesResponseDto> getLikedGyms(Long userId) {
        List<GymLike> gymLikes = gymLikeLoadPort.findByUserId(userId);

        return gymLikes.stream()
                .map(GymLikesResponseDto::of)
                .toList();
    }

    @Override
    @Transactional
    public void unlikeGym(GymUnlikeRequestDto request) {
        GymLike gymLike = gymLikeLoadPort.getByUserIdAndGymName(request.getUserId(), request.getGymName());
        gymLikeUpdatePort.delete(gymLike);
    }

    @Override
    public List<GymSearchResponseDto> searchGyms(String keyword) {
        List<Gym> gyms = gymLoadPort.searchByName(keyword);

        return gyms.stream()
                .map(GymSearchResponseDto::of)
                .toList();
    }

    @Override
    public List<String> autoCorrectGymNames(String keyword, int size) {
        return gymLoadPort.autoCorrectGymNames(keyword, size);
    }
}
