package swm.s3.coclimb.api.application.port.in.media;

import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;

public interface MediaCommand {
    void createMedia(MediaCreateRequestDto mediaCreateRequestDto);
}
