package swm.s3.coclimb.api.application.port.in.media;

import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaDeleteRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaUpdateRequestDto;
import swm.s3.coclimb.api.application.port.out.aws.dto.S3AccessToken;

public interface MediaCommand {
    void createMedia(MediaCreateRequestDto mediaCreateRequestDto);

    void updateMedia(MediaUpdateRequestDto mediaUpdateRequestDto);
    void deleteMedia(MediaDeleteRequestDto mediaDeleteRequestDto);

    S3AccessToken createS3AccessToken(String bucket, String prefix, Long userId, String action);
}
