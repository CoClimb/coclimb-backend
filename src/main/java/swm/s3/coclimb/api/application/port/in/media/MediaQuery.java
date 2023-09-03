package swm.s3.coclimb.api.application.port.in.media;

import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaPageRequestDto;
import swm.s3.coclimb.domain.media.Media;

import java.util.List;

public interface MediaQuery {
    List<InstagramMediaResponseDto> getMyInstagramVideos(String accessToken);

    Page<Media> getPagedMedias(MediaPageRequestDto requestDto);

    Page<Media> getPagedMediasByUserId(Long userId, MediaPageRequestDto requestDto);

    Media getMediaById(Long mediaId);
}
