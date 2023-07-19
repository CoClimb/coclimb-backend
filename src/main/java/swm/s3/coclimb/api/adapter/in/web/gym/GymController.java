package swm.s3.coclimb.api.adapter.in.web.gym;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import swm.s3.coclimb.api.ApiResponse;
import swm.s3.coclimb.api.application.port.in.gym.GymCommand;
import swm.s3.coclimb.api.application.port.in.gym.GymInfoResponseDto;
import swm.s3.coclimb.api.application.port.in.gym.GymQuery;
import swm.s3.coclimb.api.exception.errortype.ValidationFail;

@Validated
@RestController
@RequiredArgsConstructor
public class GymController {
    private final GymCommand gymCommand;
    private final GymQuery gymQuery;

    @PostMapping("/gyms")
    public ResponseEntity<ApiResponse<Void>> createGym(@RequestBody @Valid GymCreateRequest request) {
        gymCommand.createGym(request.toServiceDto());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("새로운 암장이 등록되었습니다."));
    }

    @DeleteMapping("/gyms")
    public ResponseEntity<ApiResponse<Void>> removeGymByName(@RequestBody @Valid GymRemoveRequest request) {
        gymCommand.removeGymByName(request.getName());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.noContent("암장이름을 제외한 암장 관련 정보가 제거되었습니다."));
    }
    @PatchMapping("/gyms")
    public ResponseEntity<ApiResponse<Void>> updateGym(@RequestBody @Valid GymUpdateRequest request) {
        gymCommand.updateGym(request.toServiceDto());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.noContent("암장 정보가 수정되었습니다."));
    }

    @GetMapping("/gyms/{name}")
    public ApiResponse<GymInfoResponseDto> getGymInfoByName(@PathVariable String name) {
        if(name.isBlank()){
            ValidationFail validationFail = ValidationFail.onRequest();
            validationFail.addField("name","암장 이름은 공백일 수 없습니다.");
            throw validationFail;
        }
        return ApiResponse.ok(gymQuery.getGymInfoByName(name));
    }
    @GetMapping("/gyms/locations")
    public ApiResponse<GymLocationsResponse> getGymLocations() {
        return ApiResponse.ok(GymLocationsResponse.of(gymQuery.getGymLocations()));
    }
}
