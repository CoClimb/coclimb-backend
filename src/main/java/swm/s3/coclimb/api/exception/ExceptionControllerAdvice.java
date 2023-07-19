package swm.s3.coclimb.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import swm.s3.coclimb.api.ApiErrorResponse;
import swm.s3.coclimb.api.exception.errortype.basetype.CustomException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiErrorResponse BindExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> fields = new HashMap<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            fields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ApiErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("요청에 유효하지 않은 값이 포함된 필드가 존재합니다.")
                .fields(fields)
                .build();
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiErrorResponse> customExceptionHandler(CustomException e){
        return ResponseEntity
                .status(e.getStatus())
                .body(ApiErrorResponse.builder()
                        .httpStatus(e.getStatus())
                        .message(e.getMessage())
                        .fields(e.getFields())
                        .build()
                );
    }
}
