package com.crazy.back.exception;

import com.crazy.back.dto.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Resource Not Found")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .path(getPath(request))
                .timestamp(OffsetDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(
            ExternalApiException ex, WebRequest request) {

        log.error("External API error from {}: {} (Status: {})",
                ex.getApiName(), ex.getMessage(), ex.getStatusCode());

        HttpStatus status = determineHttpStatus(ex.getStatusCode());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("External Service Error")
                .message(ex.getMessage())
                .status(status.value())
                .path(getPath(request))
                .timestamp(OffsetDateTime.now())
                .details(List.of("API: " + ex.getApiName(), "Original Status: " + ex.getStatusCode()))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(InvalidSortFieldException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSortField(
            InvalidSortFieldException ex, WebRequest request) {

        log.warn("Invalid sort field: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Invalid Sort Parameter")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(OffsetDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Validation error: {}", ex.getMessage());

        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Validation Failed")
                .message("Invalid request parameters")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(OffsetDateTime.now())
                .details(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        log.warn("Type mismatch error: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Invalid Parameter Type")
                .message(String.format("Parameter '%s' has invalid value '%s'",
                        ex.getName(), ex.getValue()))
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(OffsetDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Invalid Argument")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(OffsetDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error: ", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(getPath(request))
                .timestamp(OffsetDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus determineHttpStatus(int externalApiStatus) {
        return switch (externalApiStatus) {
            case 400, 404 -> HttpStatus.NOT_FOUND;
            case 429 -> HttpStatus.TOO_MANY_REQUESTS;
            case 500, 502, 503, 504 -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.BAD_GATEWAY;
        };
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
