package swm.s3.coclimb.api.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import swm.s3.coclimb.api.exception.errortype.basetype.CustomException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse BindExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> fields = new HashMap<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            if (fieldError.getCode().equals(NotNull.class.getName())) {
                fields.put(fieldError.getField(), FieldErrorType.NOT_NULL);
            } else if (fieldError.getCode().equals(NotEmpty.class.getName())) {
                fields.put(fieldError.getField(), FieldErrorType.NOT_EMPTY);
            } else if (fieldError.getCode().equals(NotBlank.class.getName())) {
                fields.put(fieldError.getField(), FieldErrorType.NOT_BLANK);
            } else {
                fields.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }
        return ErrorResponse.builder()
                .message("유효하지 않은 필드가 존재합니다.")
                .fields(fields)
                .build();
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e){
                return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponse.builder()
                        .message(e.getMessage())
                        .fields(e.getFields())
                        .build()
                );
    }
}
