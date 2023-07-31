package swm.s3.coclimb.api.exception;

public class FieldErrorType {
    // 400:BAD_REQUEST
    public static String NOT_NULL = "must not be null";
    public static String NOT_EMPTY = "must not be empty";
    public static String NOT_BLANK = "must not be blank";
    public static String MIN(int value) {
        return "must be greater than or equal to " + value;
    }
    public static String MAX(int value) {
        return "must be less than or equal to " + value;
    }

    public static String INVALID_VALUE = "is invalid value";

    // 404:NOT_FOUND
    public static String NOT_MATCH = "does not match";

    // 409:CONFLICT
    public static String DUPLICATED = "must not be duplicated";
}
