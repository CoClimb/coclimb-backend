package swm.s3.coclimb.api.adapter.in.web.gym;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.s3.coclimb.api.adapter.in.web.gym.dto.*;
import swm.s3.coclimb.api.application.port.in.gym.GymCommand;
import swm.s3.coclimb.api.application.port.in.gym.GymQuery;
import swm.s3.coclimb.api.application.port.in.gym.dto.*;
import swm.s3.coclimb.api.exception.FieldErrorType;
import swm.s3.coclimb.api.exception.errortype.ValidationFail;
import swm.s3.coclimb.config.argumentresolver.LoginUser;
import swm.s3.coclimb.config.interceptor.Auth;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.user.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GymController {
    private final GymCommand gymCommand;
    private final GymQuery gymQuery;

    @Auth
    @PostMapping("/gyms")
    public ResponseEntity<Void> createGym(@RequestBody @Valid GymCreateRequest request) {
        gymCommand.createGym(request.toServiceDto());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Auth
    @DeleteMapping("/gyms")
    public ResponseEntity<Void> removeGymByName(@RequestBody @Valid GymRemoveRequest request) {
        gymCommand.removeGymByName(request.getName());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Auth
    @PatchMapping("/gyms")
    public ResponseEntity<Void> updateGym(@RequestBody @Valid GymUpdateRequest request) {
        if (request.getUpdateName().isBlank()) {
            throw ValidationFail.onRequest()
                    .addField("updateName", FieldErrorType.NOT_BLANK);
        }
        gymCommand.updateGym(request.toServiceDto());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/gyms/info/{name}")
    public ResponseEntity<GymInfoResponseDto> getGymInfoByName(@PathVariable String name) {
        if(name.isBlank()){
            throw ValidationFail.onRequest()
                    .addField("name", FieldErrorType.NOT_BLANK);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gymQuery.getGymInfoByName(name));
    }
    @GetMapping("/gyms/locations")
    public ResponseEntity<GymLocationsResponse> getGymLocations() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GymLocationsResponse.of(gymQuery.getGymLocations()));
    }

    @GetMapping("/gyms")
    public ResponseEntity<GymPageResponse> getPagedGyms(@RequestParam(required = false, defaultValue = "0") int page,
                                                     @RequestParam(required = false, defaultValue = "10") int size) {
        if (page < 0) {
            throw ValidationFail.onRequest()
                    .addField("page", FieldErrorType.MIN(0));
        }
        Page<Gym> pagedGyms = gymQuery.getPagedGyms(GymPageRequestDto.builder()
                .page(page)
                .size(size)
                .build());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GymPageResponse.of(pagedGyms));
    }

    @GetMapping("/gyms/nearby")
    public ResponseEntity<GymNearbyResponse> getNearbyGyms(@RequestParam @Min(-90)  @Max(90) float latitude,
                                                           @RequestParam @Min(-180) @Max(180) float longitude,
                                                           @RequestParam @Min(0) @Max(30) float distance) {

        List<GymNearbyResponseDto> nearbyGyms = gymQuery.getNearbyGyms(latitude, longitude, distance);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GymNearbyResponse.of(nearbyGyms));
    }

    @PostMapping("/gyms/likes")
    public ResponseEntity<Void> likeGym(@RequestBody @Valid GymLikeRequest request, @LoginUser User user) {
        gymCommand.likeGym(request.toServiceDto(user));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/gyms/likes")
    public ResponseEntity<GymLikesResponse> getLikedGyms(@LoginUser User user) {
        List<GymLikesResponseDto> likedGyms = gymQuery.getLikedGyms(user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GymLikesResponse.of(likedGyms));
    }

    @DeleteMapping("/gyms/likes")
    public ResponseEntity<Void> unlikeGym(@RequestBody @Valid GymUnlikeRequest request, @LoginUser User user) {
        gymCommand.unlikeGym(request.toServiceDto(user.getId()));

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/gyms/demo/search")
    public ResponseEntity<GymSearchResponse> searchGyms(@RequestParam @NotNull(message = "keyword is requㅇred") String keyword) {
        List<GymSearchResponseDto> gyms = gymQuery.searchGyms(keyword);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GymSearchResponse.of(gyms));
    }

    @GetMapping("/gyms/autocorrect")
    public ResponseEntity<GymNameAutoCorrectResponse> getAutoCorrectedGymNames(@RequestParam @NotNull(message = "keyword is requㅇred") String keyword) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GymNameAutoCorrectResponse(gymQuery.autoCorrectGymNames(keyword,10)));
    }
}
