package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.adapter.out.filestore.AwsCloudFrontManager;
import swm.s3.coclimb.api.adapter.out.filestore.AwsSTSManager;
import swm.s3.coclimb.api.adapter.out.oauth.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.application.port.in.media.MediaCommand;
import swm.s3.coclimb.api.application.port.in.media.MediaQuery;
import swm.s3.coclimb.api.application.port.in.media.dto.*;
import swm.s3.coclimb.api.application.port.out.filestore.FileStoreLoadPort;
import swm.s3.coclimb.api.application.port.out.filestore.FileStoreUpdatePort;
import swm.s3.coclimb.api.application.port.out.filestore.dto.MediaUploadUrl;
import swm.s3.coclimb.api.application.port.out.oauth.instagram.InstagramDataPort;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaUpdatePort;
import swm.s3.coclimb.api.exception.errortype.media.InstagramMediaIdConflict;
import swm.s3.coclimb.api.exception.errortype.media.InvalidMediaUrl;
import swm.s3.coclimb.api.exception.errortype.media.MediaNotFound;
import swm.s3.coclimb.api.exception.errortype.media.MediaOwnerNotMatched;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MediaService implements MediaQuery, MediaCommand {

    private final InstagramDataPort instagramDataPort;
    private final MediaLoadPort mediaLoadPort;
    private final MediaUpdatePort mediaUpdatePort;

    private final FileStoreLoadPort fileStoreLoadPort;
    private final FileStoreUpdatePort fileStoreUpdatePort;

    private final AwsCloudFrontManager awsCloudFrontManager;


    @Override
    public List<InstagramMediaResponseDto> getMyInstagramVideos(String accessToken) {
        List<InstagramMediaResponseDto> myMedias = instagramDataPort.getMyMedias(accessToken);
        List<InstagramMediaResponseDto> myVideos = new ArrayList<InstagramMediaResponseDto>();

        for (InstagramMediaResponseDto media : myMedias) {
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
        if (instagramMediaInfo != null && isInstagramMediaIdDuplicated(instagramMediaInfo.getId())) {
            throw new InstagramMediaIdConflict();
        }
        mediaUpdatePort.save(mediaCreateRequestDto.toEntity(
                extractKeyFrom(mediaCreateRequestDto.getVideoUrl()),
                extractKeyFrom(mediaCreateRequestDto.getThumbnailUrl())));
    }

    private String extractKeyFrom(String url){
        String regex = ".+/([^?]+)/([^?]+)/([^?]+)\\?.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return String.format("%s/%s/%s", matcher.group(1), matcher.group(2), matcher.group(3));
        } else {
            throw new InvalidMediaUrl();
        }
    }

    private boolean isInstagramMediaIdDuplicated(String instagramMediaId) {
        return mediaLoadPort.findByInstagramMediaId(instagramMediaId).isPresent();
    }


    @Override
    public Page<MediaInfo> getPagedMedias(MediaPageRequest request) {
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize());
        Page<Media> page;
        if (request.getGymName() != null && request.getUserName() != null) {
            page = mediaLoadPort.findPagedByGymNameAndUserName(request.getGymName(), request.getUserName(), pageRequest);
        } else if (request.getGymName() != null) {
            page = mediaLoadPort.findPagedByGymName(request.getGymName(), pageRequest);
        } else if (request.getUserName() != null) {
            page = mediaLoadPort.findPagedByUserName(request.getUserName(), pageRequest);
        } else {
            page = mediaLoadPort.findAllPaged(pageRequest);
        }

        return getSignedPage(page);
    }

    @Override
    public MediaInfo getMediaById(Long mediaId) {
        return signUrl(mediaLoadPort.findById(mediaId).orElseThrow(MediaNotFound::new));
    }

    private PageImpl<MediaInfo> getSignedPage(Page<Media> page) {
        return new PageImpl<>(page.getContent().stream().map(this::signUrl).toList(),
                PageRequest.of(page.getNumber(), page.getSize()),
                page.getTotalElements());
    }

    private MediaInfo signUrl(Media media) {

        return MediaInfo.of(media,
                awsCloudFrontManager.getSignedUrl(media.getVideoKey()).url(),
                awsCloudFrontManager.getSignedUrl(media.getThumbnailKey()).url());
    }

    @Override
    @Transactional
    public void updateMedia(MediaUpdateRequestDto mediaUpdateRequestDto) {
        Media media = mediaLoadPort.findById(mediaUpdateRequestDto.getMediaId()).orElseThrow(MediaNotFound::new);
        if (!media.getUser().getId().equals(mediaUpdateRequestDto.getUser().getId())) {
            throw new MediaOwnerNotMatched();
        }
        media.update(mediaUpdateRequestDto.getDescription());
    }

    @Override
    @Transactional
    public void deleteMedia(MediaDeleteRequestDto mediaDeleteRequestDto) {
        Media media = mediaLoadPort.findById(mediaDeleteRequestDto.getMediaId()).orElseThrow(MediaNotFound::new);
        if (!media.getUser().getId().equals(mediaDeleteRequestDto.getUser().getId())) {
            throw new MediaOwnerNotMatched();
        }

        fileStoreUpdatePort.deleteFile(media.getVideoKey());
        fileStoreUpdatePort.deleteFile(media.getThumbnailKey());
        mediaUpdatePort.delete(media);
    }

    @Override
    public MediaUploadUrl getUploadUrl(Long userId) {
        UUID uuid = UUID.randomUUID();

        return MediaUploadUrl.builder()
                .videoUploadUrl(fileStoreLoadPort.getUploadUrl(String.format("%s/%s/%s.%s", userId, "video", uuid, "mp4")).toString())
                .thumbnailUploadUrl(fileStoreLoadPort.getUploadUrl(String.format("%s/%s/%s.%s", userId, "thumbnail", uuid, "jpg")).toString())
                .build();
    }

}
