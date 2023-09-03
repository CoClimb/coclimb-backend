package swm.s3.coclimb.api.application.port.out.persistence.media;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import swm.s3.coclimb.domain.media.Media;

import java.util.Optional;

public interface MediaLoadPort {

    Optional<Media> findByInstagramMediaId(String instagramMediaId);

    Page<Media> findAllPaged(PageRequest pageRequest);

    Page<Media> findPagedByUserId(Long userId, PageRequest pageRequest);

    Optional<Media> findById(Long mediaId);
}
