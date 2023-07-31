package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.MediaCommand;
import swm.s3.coclimb.api.application.port.in.media.MediaQuery;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaInfoDto;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaUpdatePort;
import swm.s3.coclimb.api.exception.errortype.media.InstagramMediaIdConflict;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MediaService implements MediaQuery, MediaCommand {

    private final InstagramRestApiManager instagramRestApiManager;
    private final MediaLoadPort mediaLoadPort;
    private final MediaUpdatePort mediaUpdatePort;

    @Override
    public List<InstagramMediaResponseDto> getMyInstagramVideos(String accessToken) {
        List<InstagramMediaResponseDto> myMedias = instagramRestApiManager.getMyMedias(accessToken);
        List<InstagramMediaResponseDto> myVideos = new ArrayList<InstagramMediaResponseDto>();

        for(InstagramMediaResponseDto media : myMedias) {
            if (media.getMediaType().equals("VIDEO")) {
                myVideos.add(media);
            }
        }

        return myVideos;
    }

    @Override
    public List<MediaInfoDto> findAll() {
        return mediaLoadPort.findAll().stream()
                .map(MediaInfoDto::of)
                .toList();
    }

    @Override
    public List<MediaInfoDto> findAllVideos() {
        return mediaLoadPort.findAllVideos().stream()
                .map(MediaInfoDto::of)
                .toList();
    }

    @Override
    public void createMedia(MediaCreateRequestDto mediaCreateRequestDto) {
        if(isInstagramMediaIdDuplicated(mediaCreateRequestDto.getInstagramMediaId())) {
            throw new InstagramMediaIdConflict();
        }
        mediaUpdatePort.save(mediaCreateRequestDto.toEntity());
    }

    private boolean isInstagramMediaIdDuplicated(String instagramMediaId) {
        return mediaLoadPort.findByInstagramMediaId(instagramMediaId).isPresent();
    }
}
