package swm.s3.coclimb.api.application.port.out.filestore.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@NoArgsConstructor
@Getter
public class MediaUploadUrl {
    String videoUploadUrl;
    String thumbnailUploadUrl;

    @Builder
    public MediaUploadUrl(String videoUploadUrl, String thumbnailUploadUrl) {
        this.videoUploadUrl = videoUploadUrl;
        this.thumbnailUploadUrl = thumbnailUploadUrl;
    }

    public static MediaUploadUrl of(String videoUploadUrl, String thumbnailUploadUrl) {
        return MediaUploadUrl.builder()
                .videoUploadUrl(videoUploadUrl)
                .thumbnailUploadUrl(thumbnailUploadUrl)
                .build();
    }
}
