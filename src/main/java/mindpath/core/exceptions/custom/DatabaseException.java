package mindpath.core.exceptions.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseException extends ExceptionHandler {
    public DatabaseException(String message) {
        super(message);
    }

}
