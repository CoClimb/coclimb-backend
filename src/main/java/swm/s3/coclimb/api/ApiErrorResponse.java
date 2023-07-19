package swm.s3.coclimb.api;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class ApiErrorResponse extends ApiCommonResponse {
    private final Map<String, String> fields;

    @Builder
    private ApiErrorResponse(HttpStatus httpStatus, String message, Map<String, String> fields) {
        super(httpStatus,message);
        this.fields = fields;
    }

}
