package swm.s3.coclimb.api.application.port.in.media;

import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaInfo;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaPageRequest;
import swm.s3.coclimb.api.application.port.out.filestore.dto.MediaUploadUrl;

import java.util.List;

public interface MediaQuery {
    List<InstagramMediaResponseDto> getMyInstagramVideos(String accessToken);

    Page<MediaInfo> getPagedMedias(MediaPageRequest requestDto);

    MediaInfo getMediaById(Long mediaId);

    MediaUploadUrl getUploadUrl(Long userId);
}
