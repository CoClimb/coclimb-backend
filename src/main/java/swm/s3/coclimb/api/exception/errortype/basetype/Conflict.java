package swm.s3.coclimb.api.exception.errortype.basetype;


import org.springframework.http.HttpStatus;

public abstract class Conflict extends CustomException {

    public Conflict(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 409;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

}
