package swm.s3.coclimb.api.exception.errortype.elasticsearch;

import swm.s3.coclimb.api.exception.errortype.basetype.BadRequest;

public class InvalidElasticQuery extends BadRequest {
    public InvalidElasticQuery() {
        super("쿼리가 유효하지 않습니다.");
//        addField("name", FieldErrorType.NOT_MATCH);
    }
}
