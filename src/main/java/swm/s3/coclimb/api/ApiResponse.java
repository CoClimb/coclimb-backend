package swm.s3.coclimb.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> extends ApiCommonResponse {
    private T data;

    public ApiResponse(HttpStatus httpStatus, String message, T data) {
        super(httpStatus, message);
        this.data = data;
    }


    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(HttpStatus.OK, message, data);
    }
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK, "요청 성공. 반환 결과는 \'data\' 필드를 확인하세요.", data);
    }
    public static <T> ApiResponse<T> created(String message) {
        return new ApiResponse<>(HttpStatus.CREATED, message, null);
    }

    public static <T> ApiResponse<T> noContent(String message) {
        return new ApiResponse<>(HttpStatus.NO_CONTENT, message, null);
    }
}
