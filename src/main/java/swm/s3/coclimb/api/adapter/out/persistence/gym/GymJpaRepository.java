package swm.s3.coclimb.api.adapter.out.persistence.gym;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swm.s3.coclimb.api.adapter.out.persistence.gym.dto.GymNearby;
import swm.s3.coclimb.domain.gym.Gym;

import java.util.List;
import java.util.Optional;

public interface GymJpaRepository extends JpaRepository<Gym, Long> {

    boolean existsByName(String name);

    Optional<Gym> findByName(String name);

    @Query(value = "select id, name, address, latitude, longitude, image_url as imageUrl, st_distance_sphere(point(longitude, latitude), point(:longitude, :latitude))/1000.0 as distance " +
            "from gyms having distance < :distance order by distance", nativeQuery = true)
    List<GymNearby> findNearby(@Param("latitude") float latitude, @Param("longitude") float longitude, @Param("distance") float distance);

    @Query(value = "select * from gyms where replace(name,' ', '') like %:keyword%", nativeQuery = true)
    List<Gym> findByNameLike(@Param("keyword") String keyword);
}
