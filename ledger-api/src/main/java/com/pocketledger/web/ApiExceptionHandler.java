package com.pocketledger.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    // 400 - bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> onBadJson(HttpServletRequest req, HttpMessageNotReadableException ex) {
        log.debug("Bad JSON: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "bad_request", "Malformed JSON or wrong types", req.getRequestURI())
        );
    }

    // 400 — на @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> onBodyValidation(HttpServletRequest req, MethodArgumentNotValidException ex) {
        List<ApiError.FieldError> errors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.add(new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()));
        }
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "validation_error", "Validation failed", req.getRequestURI(), errors)
        );
    }

    // 400 — @Validated на параметрах методов
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> onParamValidation(HttpServletRequest req, ConstraintViolationException ex) {
        var details = ex.getConstraintViolations().stream()
                .map(v -> new ApiError.FieldError(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "validation_error", "Validation failed", req.getRequestURI(), details)
        );
    }

    // 400 — типы не совпали
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> onTypeMismatch(HttpServletRequest req, MethodArgumentTypeMismatchException ex) {
        String msg = "Parameter '%s' has invalid value".formatted(ex.getName());
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "type_mismatch", msg, req.getRequestURI())
        );
    }

    // 400 — отсутствует обязательный query-параметр
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> onMissingParam(HttpServletRequest req, MissingServletRequestParameterException ex) {
        String msg = "Missing required parameter '%s'".formatted(ex.getParameterName());
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "missing_parameter", msg, req.getRequestURI())
        );
    }

    // 404 — NotFound
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> onNotFound(HttpServletRequest req, NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.of(404, "not_found", ex.getMessage(), req.getRequestURI())
        );
    }

    // 404 — Optional.get()/findById().orElseThrow(NoSuchElementException)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> onNoSuchElement(HttpServletRequest req, NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.of(404, "not_found", "resource not found", req.getRequestURI())
        );
    }

    // 403 — недостаточно прав
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> onAccessDenied(HttpServletRequest req, AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiError.of(403, "forbidden", "access denied", req.getRequestURI())
        );
    }

    // 409 — нарушения ограничений БД
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> onDataIntegrity(HttpServletRequest req, DataIntegrityViolationException ex) {
        log.debug("Data integrity violation", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiError.of(409, "conflict", "data integrity violation", req.getRequestURI())
        );
    }

    // 400 — JPA bean validation через транзакцию
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiError> onTxSystem(HttpServletRequest req, TransactionSystemException ex) {
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "validation_error", "Validation failed", req.getRequestURI())
        );
    }

    // 500 — всё остальное
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> onAny(HttpServletRequest req, Exception ex) {
        log.error("Unhandled error on {}: {}", req.getRequestURI(), ex.toString(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiError.of(500, "internal_error", "internal server error", req.getRequestURI())
        );
    }
}
