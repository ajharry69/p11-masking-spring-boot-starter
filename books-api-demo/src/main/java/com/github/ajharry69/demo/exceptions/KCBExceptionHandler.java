package com.github.ajharry69.demo.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class KCBExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleException(Exception exception, HttpServletRequest request) {
        log.error(exception.getMessage(), exception);

        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now());
        payload.put("errorCode", "SERVER_ERROR");

        Problem.ExtendedProblem<Map<String, Object>> problem = Problem.create()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .withProperties(payload)
                .withDetail(exception.getMessage())
                .withInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(problem);
    }

    @ResponseBody
    @ExceptionHandler(KCBException.class)
    public ResponseEntity<Problem> handle(KCBException exception, HttpServletRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now());
        payload.put("errorCode", exception.getErrorCode());

        Problem.ExtendedProblem<Map<String, Object>> problem = Problem.create()
                .withStatus(HttpStatus.valueOf(exception.getStatusCode().value()))
                .withProperties(payload)
                .withDetail(exception.getMessage())
                .withInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(exception.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(problem);
    }
}