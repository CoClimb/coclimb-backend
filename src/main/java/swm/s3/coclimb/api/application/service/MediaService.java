package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.adapter.out.aws.AwsSTSManager;
import swm.s3.coclimb.api.application.port.out.aws.dto.S3AccessToken;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.MediaCommand;
import swm.s3.coclimb.api.application.port.in.media.MediaQuery;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaDeleteRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaPageRequestDto;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaUpdateRequestDto;
import swm.s3.coclimb.api.application.port.out.aws.AwsS3UpdatePort;
import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;
import swm.s3.coclimb.api.application.port.out.filedownload.FileDownloadPort;
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

    private final FileDownloadPort fileDownloadPort;
    private final AwsS3UpdatePort awsS3UpdatePort;

    private final AwsSTSManager awsSTSManager;
    private static final String WRITE_ACTION = "PutObject";
    private static final String READ_ACTION = "Get*";

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

        DownloadedFileDetail mediaFile = fileDownloadPort.downloadFile(mediaCreateRequestDto.getMediaUrl());
        String mediaUrl = awsS3UpdatePort.uploadFile(mediaFile);

        DownloadedFileDetail thumbnailFile = fileDownloadPort.downloadFile(mediaCreateRequestDto.getThumbnailUrl());
        String thumbnailUrl = awsS3UpdatePort.uploadFile(thumbnailFile);

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
    public Page<Media> getPagedMediasByGymName(String gymName, MediaPageRequestDto requestDto) {
        PageRequest pageRequest = PageRequest.of(
                requestDto.getPage(),
                requestDto.getSize());

        return mediaLoadPort.findPagedByGymName(gymName, pageRequest);
    }

    @Override
    public Page<Media> getPagedMediasByUserName(String userName, MediaPageRequestDto requestDto) {
        PageRequest pageRequest = PageRequest.of(
                requestDto.getPage(),
                requestDto.getSize());

        return mediaLoadPort.findPagedByUserName(userName, pageRequest);
    }

    @Override
    public Page<Media> getPagedMediasByGymNameAndUserName(String gymName, String userName, MediaPageRequestDto requestDto) {
        PageRequest pageRequest = PageRequest.of(
                requestDto.getPage(),
                requestDto.getSize());

        return mediaLoadPort.findPagedByGymNameAndUserName(gymName, userName, pageRequest);
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

        awsS3UpdatePort.deleteFile(media.getMediaUrl());
        awsS3UpdatePort.deleteFile(media.getThumbnailUrl());
        mediaUpdatePort.delete(media);
    }

    @Override
    public S3AccessToken createTokenForUpload(String bucket, String prefix, Long userId){
        String key = awsSTSManager.generateKey(prefix, userId);
        String policy = awsSTSManager.generatePolicy(bucket, key, WRITE_ACTION);
        return S3AccessToken
                .of(bucket, key, awsSTSManager.getUploadCredentials(policy));
    }
}
