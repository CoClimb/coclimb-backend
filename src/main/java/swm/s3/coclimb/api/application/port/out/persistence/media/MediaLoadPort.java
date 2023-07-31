package swm.s3.coclimb.api.application.port.out.persistence.media;

import swm.s3.coclimb.domain.media.Media;

import java.util.List;
import java.util.Optional;

public interface MediaLoadPort {
    List<Media> findAll();
    List<Media> findAllVideos();

    Optional<Media> findByInstagramMediaId(String instagramMediaId);
}
