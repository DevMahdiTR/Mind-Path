package mindpath.core.exceptions.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRoleException extends ExceptionHandler{
    public InvalidRoleException(String message) {
        super(message);
    }
}
