package swm.s3.coclimb.api.adapter.out.persistence.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.media.MediaUpdatePort;
import swm.s3.coclimb.domain.media.Media;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MediaRepository implements MediaLoadPort, MediaUpdatePort {

    private final MediaJpaRepository mediaJpaRepository;

    @Override
    public List<Media> findAll() {
        return mediaJpaRepository.findAll();
    }

    @Override
    public List<Media> findAllVideos() {
        return mediaJpaRepository.findAllVideos();
    }

    @Override
    public void save(Media media) {
        mediaJpaRepository.save(media);
    }

    @Override
    public Optional<Media> findByInstagramMediaId(String instagramMediaId) {
        return mediaJpaRepository.findByInstagramMediaInfoId(instagramMediaId);
    }
}
