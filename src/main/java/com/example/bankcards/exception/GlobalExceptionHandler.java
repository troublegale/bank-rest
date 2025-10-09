package com.example.bankcards.exception;

import com.example.bankcards.dto.AppError;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<String> messages = ex.getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .toList();

        AppError error = new AppError(400, messages, new Date());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<AppError> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex) {
        String message = String.format("Unknown field: %s", ex.getPropertyName());
        AppError error = new AppError(400, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        AppError error = new AppError(400, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<AppError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        String message = ex.getMessage();
        AppError error = new AppError(404, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AppError> handleBadCredentialsException(BadCredentialsException ignored) {
        String message = "Wrong password";
        AppError error = new AppError(401, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<AppError> handleNotAllowedException(NotAllowedException ex) {
        String message = ex.getMessage();
        AppError error = new AppError(403, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<AppError> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = ex.getMessage();
        AppError error = new AppError(405, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<AppError> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String message = "Endpoint not found";
        AppError error = new AppError(404, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailTakenException.class)
    public ResponseEntity<AppError> handleEmailTakenException(EmailTakenException ex) {
        String message = ex.getMessage();
        AppError error = new AppError(409, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<AppError> handleNoSuchElementException(NoSuchElementException ex) {
        String message = ex.getMessage();
        AppError error = new AppError(404, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidSearchQueryException.class)
    public ResponseEntity<AppError> handleInvalidSearchQueryException(InvalidSearchQueryException ex) {
        String message = ex.getMessage();
        AppError error = new AppError(400, List.of(message), new Date());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
