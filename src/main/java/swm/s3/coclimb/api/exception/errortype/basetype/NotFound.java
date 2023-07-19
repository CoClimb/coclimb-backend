package swm.s3.coclimb.api.exception.errortype.basetype;


import org.springframework.http.HttpStatus;

public abstract class NotFound extends CustomException {

    public NotFound(String message) {
        super(message);
    }

    public NotFound(){
        super("객체를 찾을 수 없습니다.");
    }
    @Override
    public int getStatusCode() {
        return 404;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
