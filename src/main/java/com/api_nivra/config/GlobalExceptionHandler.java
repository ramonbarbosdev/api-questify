package com.api_nivra.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.api_nivra.dto.ApiErrorResponse;
import com.api_nivra.dto.FieldErrorDTO;
import com.api_nivra.exception.BusinessException;
import com.api_nivra.exception.ConflictException;
import com.api_nivra.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // VALIDATION (400)
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleValidation(
                        ConstraintViolationException ex,
                        HttpServletRequest request) {

                List<FieldErrorDTO> errors = ex.getConstraintViolations()
                                .stream()
                                .map(v -> new FieldErrorDTO(
                                                v.getPropertyPath().toString(),
                                                v.getMessage()))
                                .toList();

                ApiErrorResponse response = ApiErrorResponse.builder()
                                .status(400)
                                .error("VALIDATION_ERROR")
                                .message("Dados inválidos")
                                .details(errors)
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();

                return ResponseEntity.badRequest().body(response);
        }

        // NOT FOUND (404)
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleNotFound(
                        ResourceNotFoundException ex,
                        HttpServletRequest request) {

                return buildResponse(404, "NOT_FOUND", ex.getMessage(), request);
        }

        // BUSINESS (422)
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiErrorResponse> handleBusiness(
                        BusinessException ex,
                        HttpServletRequest request) {

                return buildResponse(422, "BUSINESS_ERROR", ex.getMessage(), request);
        }

        // CONFLICT (409)
        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ApiErrorResponse> handleConflict(
                        ConflictException ex,
                        HttpServletRequest request) {

                return buildResponse(409, "CONFLICT", ex.getMessage(), request);
        }

        // GENERIC (500)
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handleGeneric(
                        Exception ex,
                        HttpServletRequest request) {

                ApiErrorResponse response = ApiErrorResponse.builder()
                                .status(500)
                                .error("INTERNAL_ERROR")
                                .message("Erro interno inesperado")
                                .debugMessage(ex.getMessage())
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();

                return ResponseEntity.status(500).body(response);
        }

        // MÉTODO CENTRAL (REUTILIZÁVEL)
        private ResponseEntity<ApiErrorResponse> buildResponse(
                        int status,
                        String error,
                        String message,
                        HttpServletRequest request) {

                ApiErrorResponse response = ApiErrorResponse.builder()
                                .status(status)
                                .error(error)
                                .message(message)
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();

                return ResponseEntity.status(status).body(response);
        }

        @ExceptionHandler(org.springframework.dao.IncorrectResultSizeDataAccessException.class)
        public ResponseEntity<ApiErrorResponse> handleNonUnique(
                        Exception ex,
                        HttpServletRequest request) {

                ApiErrorResponse response = ApiErrorResponse.builder()
                                .status(409)
                                .error("DATA_INCONSISTENCY")
                                .message("Mais de um resultado encontrado para este desafio")
                                .debugMessage(ex.getMessage())
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();

                return ResponseEntity.status(409).body(response);
        }

        @ExceptionHandler(org.springframework.transaction.TransactionSystemException.class)
        public ResponseEntity<ApiErrorResponse> handleTransaction(
                        org.springframework.transaction.TransactionSystemException ex,
                        HttpServletRequest request) {

                Throwable root = ex.getRootCause();

                String message = "Erro ao salvar dados";

                if (root instanceof jakarta.validation.ConstraintViolationException validationEx) {

                        List<FieldErrorDTO> errors = validationEx.getConstraintViolations()
                                        .stream()
                                        .map(v -> new FieldErrorDTO(
                                                        v.getPropertyPath().toString(),
                                                        v.getMessage()))
                                        .toList();

                        ApiErrorResponse response = ApiErrorResponse.builder()
                                        .status(400)
                                        .error("VALIDATION_ERROR")
                                        .message("Dados inválidos")
                                        .details(errors)
                                        .path(request.getRequestURI())
                                        .timestamp(LocalDateTime.now())
                                        .build();

                        return ResponseEntity.badRequest().body(response);
                }

                // fallback
                return buildResponse(
                                500,
                                "TRANSACTION_ERROR",
                                message,
                                request);
        }

        @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
                        org.springframework.web.bind.MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                List<FieldErrorDTO> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> new FieldErrorDTO(
                                                error.getField(),
                                                error.getDefaultMessage()))
                                .toList();

                ApiErrorResponse response = ApiErrorResponse.builder()
                                .status(400)
                                .error("VALIDATION_ERROR")
                                .message("Dados inválidos")
                                .details(errors)
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();

                return ResponseEntity.badRequest().body(response);
        }

}