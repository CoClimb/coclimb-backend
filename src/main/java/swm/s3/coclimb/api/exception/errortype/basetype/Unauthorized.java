package swm.s3.coclimb.api.exception.errortype.basetype;


import org.springframework.http.HttpStatus;

public abstract class Unauthorized extends CustomException {

    public Unauthorized(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

}
