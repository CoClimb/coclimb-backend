package swm.s3.coclimb.api.application.port.out.persistence.media;

import swm.s3.coclimb.domain.media.Media;

public interface MediaUpdatePort {
    void save(Media media);
}
