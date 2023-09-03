package swm.s3.coclimb.api.adapter.out.persistence.media;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import swm.s3.coclimb.domain.media.Media;

import java.util.List;
import java.util.Optional;

public interface MediaJpaRepository extends JpaRepository<Media, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Media> findByIdNotNull(PageRequest pageRequest);
    Optional<Media> findByInstagramMediaInfoId(String instagramMediaId);

    @EntityGraph(attributePaths = {"user"})
    Page<Media> findPagedByUserId(Long userId, PageRequest pageRequest);

    List<Media> findByUserId(Long userId);
}
