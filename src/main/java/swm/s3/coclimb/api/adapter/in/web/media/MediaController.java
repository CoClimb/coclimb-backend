package swm.s3.coclimb.api.adapter.in.web.media;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import swm.s3.coclimb.api.adapter.in.web.media.dto.*;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.MediaCommand;
import swm.s3.coclimb.api.application.port.in.media.MediaQuery;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaDeleteRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaPageRequestDto;
import swm.s3.coclimb.api.exception.FieldErrorType;
import swm.s3.coclimb.api.exception.errortype.ValidationFail;
import swm.s3.coclimb.config.argumentresolver.LoginUser;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.user.User;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class MediaController {

    private final MediaQuery mediaQuery;
    private final MediaCommand mediaCommand;
    private static final String WRITE_ACTION = "PutObject";


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
    public ResponseEntity<MediaPageResponse> getPagedMedias(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "null") String gymName,
                                                          @RequestParam(defaultValue = "null") String userName) {
        if (page < 0) {
            throw ValidationFail.onRequest()
                    .addField("page", FieldErrorType.MIN(0));
        }

        Page<Media> pagedMedias;

        if(!gymName.equals("null") && !userName.equals("null")) {
            pagedMedias = mediaQuery.getPagedMediasByGymNameAndUserName(gymName, userName, MediaPageRequestDto
                    .builder()
                    .page(page)
                    .size(size)
                    .build());
        } else if(!gymName.equals("null")) {
                pagedMedias = mediaQuery.getPagedMediasByGymName(gymName, MediaPageRequestDto.builder()
                        .page(page)
                        .size(size)
                        .build());
        } else if(!userName.equals("null")) {
            pagedMedias = mediaQuery.getPagedMediasByUserName(userName, MediaPageRequestDto.builder()
                    .page(page)
                    .size(size)
                    .build());
        } else {
            pagedMedias = mediaQuery.getPagedMedias(MediaPageRequestDto.builder()
                    .page(page)
                    .size(size)
                    .build());
        }

        return ResponseEntity.ok(MediaPageResponse.of(pagedMedias));
    }

    @GetMapping("/medias/{id}")
    public ResponseEntity<MediaInfoResponse> getMediaInfo(@PathVariable Long id) {
        Media media = mediaQuery.getMediaById(id);
        return ResponseEntity.ok(MediaInfoResponse.of(media));
    }

    @DeleteMapping("/medias/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id, @LoginUser User user) {
        mediaCommand.deleteMedia(MediaDeleteRequestDto.builder()
                .mediaId(id)
                .user(user)
                .build());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping("/medias/{id}")
    public ResponseEntity<Void> updateMedia(@PathVariable Long id,
                                            @RequestBody @Valid MediaUpdateRequest mediaUpdateRequest,
                                            @LoginUser User user) {

        mediaCommand.updateMedia(mediaUpdateRequest.toServiceDto(id, user));

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/medias/upload")
    public ResponseEntity<MediaUploadUrlResponse> getMediaUploadUrl(@LoginUser User user) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MediaUploadUrlResponse.of(
                        mediaQuery.getUploadUrl(user.getId())));
    }
}
