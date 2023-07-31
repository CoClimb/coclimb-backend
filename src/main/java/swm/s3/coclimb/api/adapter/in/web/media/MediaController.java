package swm.s3.coclimb.api.adapter.in.web.media;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import swm.s3.coclimb.api.adapter.in.web.media.dto.InstagramMyVideosResponse;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaCreateRequest;
import swm.s3.coclimb.api.adapter.in.web.media.dto.MediaInfosResponse;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.MediaCommand;
import swm.s3.coclimb.api.application.port.in.media.MediaQuery;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaInfoDto;
import swm.s3.coclimb.config.argumentresolver.LoginUser;
import swm.s3.coclimb.domain.user.User;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class MediaController {

    private final MediaQuery mediaQuery;
    private final MediaCommand mediaCommand;

    @PostMapping("/medias")
    public ResponseEntity<Void> createMedia(@RequestBody @Valid MediaCreateRequest mediaCreateRequest, @LoginUser User user) {
        mediaCommand.createMedia(mediaCreateRequest.toServiceDto(user));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }


    @GetMapping("/medias/instagram/my-videos")
    public ResponseEntity<InstagramMyVideosResponse> getMyInstagramVideos(@LoginUser User user) {
        List<InstagramMediaResponseDto> myInstagramVideos = mediaQuery.getMyInstagramVideos(user.getInstagramUserInfo().getAccessToken());
        return ResponseEntity.ok(InstagramMyVideosResponse.of(myInstagramVideos));
    }

    @GetMapping("/medias")
    public ResponseEntity<MediaInfosResponse> getAllMedias(@RequestParam(required = false) String mediaType) {
        List<MediaInfoDto> mediaInfos;

        switch (mediaType == null ? "ALL" : mediaType.toUpperCase()) {
            case "VIDEO":
                mediaInfos = mediaQuery.findAllVideos();
                break;

            case "ALL":
            default:
                mediaInfos = mediaQuery.findAll();
                break;
        }

        return ResponseEntity.ok(MediaInfosResponse.of(mediaInfos));
    }
}
