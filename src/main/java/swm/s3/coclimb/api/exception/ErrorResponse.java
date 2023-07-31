package swm.s3.coclimb.api.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public final class ErrorResponse {
    private final String message;
    private final Map<String, String> fields;

    @Builder
    private ErrorResponse(String message, Map<String, String> fields) {
        this.message = message;
        this.fields = fields;
    }
}
