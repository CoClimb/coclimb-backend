package swm.s3.coclimb.api.application.port.in.media;

import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaDeleteRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaUpdateRequestDto;

public interface MediaCommand {
    void createMedia(MediaCreateRequestDto mediaCreateRequestDto);

    void updateMedia(MediaUpdateRequestDto mediaUpdateRequestDto);
    void deleteMedia(MediaDeleteRequestDto mediaDeleteRequestDto);
}
