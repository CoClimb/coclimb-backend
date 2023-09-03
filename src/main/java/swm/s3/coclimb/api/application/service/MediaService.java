package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.MediaCommand;
import swm.s3.coclimb.api.application.port.in.media.MediaQuery;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaPageRequestDto;
import swm.s3.coclimb.api.application.port.out.instagram.InstagramDataPort;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaUpdatePort;
import swm.s3.coclimb.api.exception.errortype.media.InstagramMediaIdConflict;
import swm.s3.coclimb.api.exception.errortype.media.MediaNotFound;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MediaService implements MediaQuery, MediaCommand {

    private final InstagramDataPort instagramDataPort;
    private final MediaLoadPort mediaLoadPort;
    private final MediaUpdatePort mediaUpdatePort;

    @Override
    public List<InstagramMediaResponseDto> getMyInstagramVideos(String accessToken) {
        List<InstagramMediaResponseDto> myMedias = instagramDataPort.getMyMedias(accessToken);
        List<InstagramMediaResponseDto> myVideos = new ArrayList<InstagramMediaResponseDto>();

        for(InstagramMediaResponseDto media : myMedias) {
            if (media.getMediaType().equals("VIDEO")) {
                myVideos.add(media);
            }
        }

        return myVideos;
    }

    @Override
    @Transactional
    public void createMedia(MediaCreateRequestDto mediaCreateRequestDto) {
        InstagramMediaInfo instagramMediaInfo = mediaCreateRequestDto.getInstagramMediaInfo();
        if(instagramMediaInfo != null && isInstagramMediaIdDuplicated(instagramMediaInfo.getId())) {
            throw new InstagramMediaIdConflict();
        }
        mediaUpdatePort.save(mediaCreateRequestDto.toEntity());
    }

    private boolean isInstagramMediaIdDuplicated(String instagramMediaId) {
        return mediaLoadPort.findByInstagramMediaId(instagramMediaId).isPresent();
    }


    @Override
    public Page<Media> getPagedMedias(MediaPageRequestDto requestDto) {
        PageRequest pageRequest = PageRequest.of(
                requestDto.getPage(),
                requestDto.getSize());

        return mediaLoadPort.findAllPaged(pageRequest);
    }

    @Override
    public Page<Media> getPagedMediasByUserId(Long userId, MediaPageRequestDto requestDto) {
        PageRequest pageRequest = PageRequest.of(
                requestDto.getPage(),
                requestDto.getSize());

        return mediaLoadPort.findPagedByUserId(userId, pageRequest);
    }

    @Override
    public Media getMediaById(Long mediaId) {
        return mediaLoadPort.findById(mediaId).orElseThrow(MediaNotFound::new);
    }
}
