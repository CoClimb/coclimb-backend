package swm.s3.coclimb.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiCommonResponse<T> {
    private boolean success;
    private int code;
    private HttpStatus status;
    private String message;

    public ApiCommonResponse(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.status = httpStatus;
        this.success = !status.isError();
        this.message = message;
    }
}
