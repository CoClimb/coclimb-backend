package swm.s3.coclimb.api.adapter.in.web.media.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.application.port.out.filestore.dto.MediaUploadUrl;

@Getter
@NoArgsConstructor
public class MediaUploadUrlResponse {
    private String videoUploadUrl;
    private String thumbnailUploadUrl;


    @Builder
    public MediaUploadUrlResponse(String videoUploadUrl, String thumbnailUploadUrl) {
        this.videoUploadUrl = videoUploadUrl;
        this.thumbnailUploadUrl = thumbnailUploadUrl;
    }

    public static MediaUploadUrlResponse of(MediaUploadUrl mediaUploadUrl) {
        return MediaUploadUrlResponse.builder()
                .videoUploadUrl(mediaUploadUrl.getVideoUploadUrl().toString())
                .thumbnailUploadUrl(mediaUploadUrl.getThumbnailUploadUrl().toString())
                .build();
    }

}
