package swm.s3.coclimb.api.exception.errortype.basetype;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class CustomException extends RuntimeException{
    private Map<String, String> fields = new HashMap<>();
    public CustomException(String message) {
        super(message);
    }

    public abstract int getStatusCode();
    public abstract HttpStatus getStatus();

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields != null ? fields : new HashMap<>();
    }
    public CustomException addField(String fieldName, String fieldMessage){
        fields.put(fieldName, fieldMessage);
        return this;
    };

}
