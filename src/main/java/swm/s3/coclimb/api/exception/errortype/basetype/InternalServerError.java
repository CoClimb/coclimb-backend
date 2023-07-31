package swm.s3.coclimb.api.exception.errortype.basetype;

import org.springframework.http.HttpStatus;

public abstract class InternalServerError extends CustomException{

        public InternalServerError(String message) {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 500;
        }

        @Override
        public HttpStatus getStatus() {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
}
