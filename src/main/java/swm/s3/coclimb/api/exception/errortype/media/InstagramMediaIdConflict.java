package swm.s3.coclimb.api.exception.errortype.media;

import swm.s3.coclimb.api.exception.errortype.basetype.Conflict;

public class InstagramMediaIdConflict extends Conflict {
    public InstagramMediaIdConflict() {
        super("해당 id의 미디어가 이미 존재합니다.");
    }
}
