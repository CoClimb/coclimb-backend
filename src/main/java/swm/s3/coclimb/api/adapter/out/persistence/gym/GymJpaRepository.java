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

    @Query(value = "SELECT ID, NAME, ADDRESS, LATITUDE, LONGITUDE, ST_DISTANCE_SPHERE(POINT(LONGITUDE, LATITUDE), POINT(:longitude, :latitude))/1000.0 AS DISTANCE " +
            "FROM gyms HAVING DISTANCE < :distance ORDER BY DISTANCE", nativeQuery = true)
    List<GymNearby> findNearby(@Param("latitude") float latitude, @Param("longitude") float longitude, @Param("distance") float distance);
}
