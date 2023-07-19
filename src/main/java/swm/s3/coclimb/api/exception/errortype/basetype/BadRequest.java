package swm.s3.coclimb.api.exception.errortype.basetype;


import org.springframework.http.HttpStatus;

public abstract class BadRequest extends CustomException {

    public BadRequest(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}
