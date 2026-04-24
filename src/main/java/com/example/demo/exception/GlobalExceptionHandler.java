package com.example.demo.exception;

import com.example.demo.dto.BfhlResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Must be the SAME email used throughout the entire application
    private static final String EMAIL = "jatin2026.be23@chitkara.edu.in";

    /**
     * Handles malformed/unparseable JSON body.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BfhlResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        BfhlResponse response = new BfhlResponse();
        response.setIs_success(false);
        response.setOfficial_email(EMAIL);
        response.setMessage("Invalid or malformed JSON body");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles wrong Content-Type header (e.g. sending text/plain instead of application/json).
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BfhlResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        BfhlResponse response = new BfhlResponse();
        response.setIs_success(false);
        response.setOfficial_email(EMAIL);
        response.setMessage("Content-Type must be application/json");
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Handles wrong argument types (e.g. passing string where number expected).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BfhlResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        BfhlResponse response = new BfhlResponse();
        response.setIs_success(false);
        response.setOfficial_email(EMAIL);
        response.setMessage("Invalid argument type: " + ex.getName());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback for any other unexpected exception.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BfhlResponse> handleGeneralException(Exception ex) {
        BfhlResponse response = new BfhlResponse();
        response.setIs_success(false);
        response.setOfficial_email(EMAIL);
        response.setMessage("Internal server error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
