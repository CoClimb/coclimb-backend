package swm.s3.coclimb.api.adapter.in.web.media.dto;

import lombok.Getter;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaInfoDto;

import java.util.List;

@Getter
public class MediaInfosResponse {

    private List<MediaInfoDto> medias;
    private int count;

    public MediaInfosResponse(List<MediaInfoDto> medias, int count) {
        this.medias = medias;
        this.count = count;
    }

    public static MediaInfosResponse of(List<MediaInfoDto> medias) {
        return new MediaInfosResponse(medias, medias.size());
    }
}
