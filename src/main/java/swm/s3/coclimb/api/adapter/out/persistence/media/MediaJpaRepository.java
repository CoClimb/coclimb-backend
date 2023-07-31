package swm.s3.coclimb.api.adapter.out.persistence.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import swm.s3.coclimb.domain.media.Media;

import java.util.List;
import java.util.Optional;

public interface MediaJpaRepository extends JpaRepository<Media, Long> {
    @Query(value = "SELECT * FROM medias WHERE media_type = 'VIDEO'", nativeQuery = true)
    List<Media> findAllVideos();

    Optional<Media> findByUserId(Long UserId);

    Optional<Media> findByInstagramMediaInfoId(String instagramMediaId);
}
