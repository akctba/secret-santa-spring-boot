package dev.secretsanta.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<String> handleApiException(ApiException exception) {
        return plainText(exception.getStatus(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    ResponseEntity<String> handleValidation(Exception exception) {
        return plainText(HttpStatus.BAD_REQUEST, "Invalid request body");
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<String> handleForbidden(AccessDeniedException exception) {
        return plainText(HttpStatus.FORBIDDEN, "User is not allowed to access this resource");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<String> handleUnexpected(Exception exception) {
        return plainText(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server-side failure");
    }

    private ResponseEntity<String> plainText(HttpStatus status, String body) {
        return ResponseEntity.status(status).contentType(MediaType.TEXT_PLAIN).body(body);
    }
}
