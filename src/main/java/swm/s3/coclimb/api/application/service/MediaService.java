package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.adapter.out.filedownload.FileDownloader;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.MediaCommand;
import swm.s3.coclimb.api.application.port.in.media.MediaQuery;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaDeleteRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaPageRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaUpdateRequestDto;
import swm.s3.coclimb.api.application.port.out.aws.AwsS3UploadPort;
import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;
import swm.s3.coclimb.api.application.port.out.instagram.InstagramDataPort;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaUpdatePort;
import swm.s3.coclimb.api.exception.errortype.media.InstagramMediaIdConflict;
import swm.s3.coclimb.api.exception.errortype.media.MediaNotFound;
import swm.s3.coclimb.api.exception.errortype.media.MediaOwnerNotMatched;
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
    private final FileDownloader fileDownloader;
    private final AwsS3UploadPort awsS3UploadPort;

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

        DownloadedFileDetail mediaFile = fileDownloader.downloadFile(mediaCreateRequestDto.getMediaUrl());
        String mediaUrl = awsS3UploadPort.uploadFile(mediaFile);

        DownloadedFileDetail thumbnailFile = fileDownloader.downloadFile(mediaCreateRequestDto.getThumbnailUrl());
        String thumbnailUrl = awsS3UploadPort.uploadFile(thumbnailFile);

        mediaUpdatePort.save(mediaCreateRequestDto.toEntity(mediaUrl, thumbnailUrl));
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

    @Override
    @Transactional
    public void updateMedia(MediaUpdateRequestDto mediaUpdateRequestDto) {
        Media media = mediaLoadPort.findById(mediaUpdateRequestDto.getMediaId()).orElseThrow(MediaNotFound::new);
        if(!media.getUser().getId().equals(mediaUpdateRequestDto.getUser().getId())) {
            throw new MediaOwnerNotMatched();
        }
        media.update(mediaUpdateRequestDto.getDescription());
    }

    @Override
    @Transactional
    public void deleteMedia(MediaDeleteRequestDto mediaDeleteRequestDto) {
        Media media = mediaLoadPort.findById(mediaDeleteRequestDto.getMediaId()).orElseThrow(MediaNotFound::new);
        if(!media.getUser().getId().equals(mediaDeleteRequestDto.getUser().getId())) {
            throw new MediaOwnerNotMatched();
        }
        mediaUpdatePort.delete(media);
    }
}
